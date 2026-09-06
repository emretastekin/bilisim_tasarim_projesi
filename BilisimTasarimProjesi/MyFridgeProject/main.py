import os
import shutil
import easyocr
import google.generativeai as genai
from fastapi import FastAPI, File, UploadFile
from pydantic import BaseModel  
from typing import List         

# --- Gemini Yapay Zeka Ayarları ---
GEMINI_API_KEY = "AQ.Ab8RN6JDE-pMhwhnM-vWdZMyN-glxe-TIBx0TsNlRLyLlTasoQ" # Kendi anahtarını unutma!
genai.configure(api_key=GEMINI_API_KEY)
model = genai.GenerativeModel('gemini-2.5-flash')

app = FastAPI(title="Akıllı Mutfak Asistanı API")

# YENİ: Mobil uygulamanın bize göndereceği "Elimdeki Malzemeler" listesinin modeli
class RecipeRequest(BaseModel):
    malzemeler: List[str]

UPLOAD_DIR = "uploads"
if not os.path.exists(UPLOAD_DIR):
    os.makedirs(UPLOAD_DIR)

print("OCR Motoru yükleniyor, lütfen bekleyin...")
reader = easyocr.Reader(['tr', 'en'], gpu=False)
print("OCR Motoru hazır!")

@app.get("/")
def read_root():
    return {"mesaj": "Mutfak Asistanı Backend Sunucusu Çalışıyor!"}

@app.post("/api/upload-receipt")
async def upload_receipt(file: UploadFile = File(...)):
    # 1. Dosyayı sunucuya kaydet
    file_location = f"{UPLOAD_DIR}/{file.filename}"
    with open(file_location, "wb") as buffer:
        shutil.copyfileobj(file.file, buffer)
    
    # 2. OCR İşlemi: Ham metni çıkar
    ocr_result = reader.readtext(file_location, detail=0) 
    raw_text = " ".join(ocr_result)
    
    # 3. Gemini ile Anlamsal Düzeltme (Prompt Mühendisliği)
    prompt = f"""
    Sen bir akıllı mutfak asistanısın. Aşağıda bir market fişinden okunmuş, hatalı ve bozuk olabilen ham metin var.
    Görevin bu metni analiz edip, alınan ürünleri tespit etmek ve sadece JSON formatında yanıt vermektir.
    Kategorileri mantıklı bir şekilde sen belirle (Sebze, Süt Ürünleri, Şarküteri, Atıştırmalık vb.).
    
    JSON formatı kesinlikle şu şekilde olmalı:
    [
      {{"urun_adi": "Domates", "miktar": "1 kg", "fiyat": "35.50", "kategori": "Sebze"}},
      {{"urun_adi": "Süt", "miktar": "1 Litre", "fiyat": "25.00", "kategori": "Süt Ürünleri"}}
    ]
    
    Sadece JSON çıktısı ver, başına veya sonuna ```json gibi markdown işaretleri veya başka hiçbir açıklama yazma.
    
    Ham Fiş Metni:
    {raw_text}
    """
    
    try:
        # Promptu modele gönderip cevabı alıyoruz
        response = model.generate_content(prompt)
        cleaned_data = response.text
    except Exception as e:
        cleaned_data = f"Yapay zeka işleme hatası: {str(e)}"
        
    return {
        "mesaj": "Fiş başarıyla işlendi ve yapay zeka ile çözümlendi.",
        "dosya_adi": file.filename,
        "ham_metin": raw_text,
        "temizlenmis_veri": cleaned_data
    }


# YENİ: Tarif Üretme Ucu (POST isteği)
@app.post("/api/recommend-recipe")
async def recommend_recipe(request: RecipeRequest):
    # Bize gelen ["Yumurta", "Domates"] listesini "Yumurta, Domates" şekline çeviriyoruz
    malzemeler_str = ", ".join(request.malzemeler)
    
    # Gemini'ye vereceğimiz sistem komutu (Prompt)
    prompt = f"""
    Sen profesyonel bir aşçısın. Elimde şu malzemeler var: {malzemeler_str}.
    Bu malzemeleri kullanarak (ekstra olarak sadece tuz, yağ, su gibi temel ev malzemeleri kullanabilirsin) 
    yapabileceğim pratik, lezzetli ve Türk damak tadına uygun bir yemek tarifi oluştur.
    
    Cevabını sadece JSON formatında ver. Başka hiçbir açıklama metni veya markdown işareti (```json) yazma. 
    Format kesinlikle şöyle olmalı:
    {{
        "yemek_adi": "Yemeğin İsmi",
        "kullanilan_malzemeler": ["malzeme 1", "malzeme 2"],
        "adimlar": ["1. adım", "2. adım"],
        "kalori_tahmini": "300 kcal"
    }}
    """
    
    try:
        # Promptu modele gönderip cevabı alıyoruz
        response = model.generate_content(prompt)
        tarif_verisi = response.text
    except Exception as e:
        tarif_verisi = f'{{"hata": "Tarif üretilemedi: {str(e)}"}}'
        
    return {
        "mesaj": "Tarif başarıyla üretildi.",
        "kullanici_malzemeleri": request.malzemeler,
        "tarif_sonucu": tarif_verisi
    }    