# بكم؟ - Bakam App
**com.webkam.app** | المبرمج احمد امليمدي

---

## هيكل المشروع

```
bakam/
├── app/src/main/
│   ├── java/com/webkam/app/
│   │   ├── BakamApp.kt                    ← Application class
│   │   ├── data/model/Models.kt           ← Data models + enums
│   │   ├── data/repository/ProductDatabase.kt ← Room DB + DAO + Repository
│   │   ├── utils/Utils.kt                 ← Preferences, Locale, Images
│   │   ├── ui/splash/SplashActivity.kt    ← شاشة HELLO
│   │   ├── ui/language/LanguageActivity.kt ← اختيار اللغة
│   │   ├── ui/main/MainActivity.kt        ← Bottom navigation
│   │   ├── ui/market/MarketFragment.kt    ← قائمة بكم
│   │   ├── ui/market/MarketAdapter.kt
│   │   ├── ui/product/ProductListActivity.kt ← منتجات السوق
│   │   ├── ui/product/ProductDetailActivity.kt ← إضافة منتج
│   │   ├── ui/product/ProductViewModel.kt
│   │   ├── ui/product/ProductAdapter.kt
│   │   ├── ui/camera/CameraActivity.kt    ← تعرف على المنتج
│   │   └── ui/settings/SettingsFragment.kt ← الإعدادات
│   └── res/
│       ├── layout/  ← جميع الـ XML layouts
│       ├── drawable/ ← Icons + shapes
│       ├── values/   ← Colors, strings, themes
│       ├── menu/     ← Bottom nav menu
│       └── xml/      ← FileProvider paths
```

---

## خطوات الإعداد في Klencod IDE

### 1. استيراد المشروع
- افتح Klencod IDE
- اختر "Open Project" → حدد مجلد `bakam/`

### 2. الخطوط (اختياري)
ضع ملفات الخطوط في `app/src/main/res/font/`:
- `SFProDisplay-Bold.ttf` ← أو اي خط مشابه
- `SFProText-Regular.ttf`

ثم عدّل في `Utils.kt` → سطر `android:fontFamily`

### 3. أذونات الكاميرا
تأكد أن `AndroidManifest.xml` يحتوي على:
```xml
<uses-permission android:name="android.permission.CAMERA"/>
<uses-permission android:name="android.permission.READ_MEDIA_IMAGES"/>
```

### 4. Build & Run
```
./gradlew assembleDebug
```
أو من IDE: **Build → Run App**

---

## ميزات التطبيق

| الميزة | الوصف |
|--------|-------|
| Splash HELLO | شاشة ترحيب بخط مزخرف |
| اختيار اللغة | عربي / English عند أول تشغيل |
| 14 سوق | من غذائية حتى جملة |
| إضافة منتج | تصوير + استرداد + اسم + حجم + سعر + جملة |
| التعرف على المنتج | CameraX + مطابقة Hash للصورة |
| الإعدادات | لغة + مشمس/مظلم + خط + واتساب |
| Room DB | حفظ محلي بدون انترنت |
| RTL/LTR | دعم كامل للعربية والإنجليزية |

---

## ملاحظات المبرمج

- الصور تُحفظ داخلياً فقط (filesDir) — لا تظهر في معرض الصور
- التعرف على المنتج يعتمد على Perceptual Hash للصورة
- دقة التعرف ≥ 75% تعتبر تطابق
- حد الوصول لواتساب: `+218934274024`

---

**المبرمج احمد امليمدي**
