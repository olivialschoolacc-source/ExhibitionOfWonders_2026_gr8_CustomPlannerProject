package calendar.main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;

public class Translator {
    private final Map<String, Locale> languageMap;
    private final Map<String, String> languageCodeMap;
    private final Map<String, String> translations;
    private final Map<String, Map<String, String>> manualTranslations;
    private final List<String> translatableTexts;

    public Translator() {
        languageMap = new LinkedHashMap<>();
        languageMap.put("English", Locale.ENGLISH);
        languageMap.put("Spanish", new Locale("es"));
        languageMap.put("French", Locale.FRENCH);
        languageMap.put("German", Locale.GERMAN);
        languageMap.put("Italian", Locale.ITALIAN);
        languageMap.put("Korean", Locale.KOREAN);
        languageMap.put("Chinese (Simplified)", Locale.SIMPLIFIED_CHINESE);
        languageMap.put("Chinese (Traditional)", Locale.TRADITIONAL_CHINESE);
        languageMap.put("Japanese", Locale.JAPANESE);
        languageMap.put("Arabic", new Locale("ar"));
        languageMap.put("Polish", new Locale("pl"));

        languageCodeMap = new LinkedHashMap<>();
        languageCodeMap.put("English", "en");
        languageCodeMap.put("Spanish", "es");
        languageCodeMap.put("French", "fr");
        languageCodeMap.put("German", "de");
        languageCodeMap.put("Italian", "it");
        languageCodeMap.put("Korean", "ko");
        languageCodeMap.put("Chinese (Simplified)", "zh");
        languageCodeMap.put("Chinese (Traditional)", "zh-TW");
        languageCodeMap.put("Japanese", "ja");
        languageCodeMap.put("Arabic", "ar");
        languageCodeMap.put("Polish", "pl");

        translations = new HashMap<>();
        translatableTexts = Arrays.asList(
            "Month:", "Year:", "Language:", "Today:", "Country:",
            "Upcoming events", "No upcoming events.", "Add Entry", "Delete Entry", "Edit Entry", "Bullet Journal",
            "Settings", "Brightness mode:", "Dark", "Light", "Save screen when resized", "Saved size:", "Not set",
            "Date:", "Time:", "Start:", "End:", "Location:", "Notes:",
            "Events for", "Events on", "Events", "Add Event", "Delete Selected",
            "Delete selected event?", "Confirm Delete", "Please enter an event name.",
            "Missing name", "End date/time must be after start date/time.", "Invalid time",
            "Add event for", "Event name:", "Start date:", "Start time:", "End date:", "End time:",
            "Close", "Save", "Cancel", "Edit Selected", "Edit event", "Holiday", "Remembrance Day",
            "Seollal", "Seollal Eve", "Seollal Holiday", "Additional Seollal Holiday", "Chuseok Eve", "Chuseok Holiday", "Calendar Application",
            "New Year's Day", "Family Day", "Good Friday", "Victoria Day", "Canada Day",
            "Civic Holiday", "Labour Day", "Thanksgiving", "Christmas Day", "Boxing Day",
            "MLK Jr. Day", "Presidents' Day", "Memorial Day", "Juneteenth", "Independence Day",
            "Labor Day", "Christmas", "Independence Movement Day", "Parliamentary Election Day",
            "Children's Day", "Buddha's Birthday", "Liberation Day",
            "National Foundation Day", "Hangeul Day", "Coming of Age Day", "Foundation Day",
            "Vernal Equinox Day", "Showa Day", "Constitution Day", "Greenery Day", "Marine Day",
            "Mountain Day", "Respect for the Aged Day", "Autumnal Equinox Day", "Sports Day",
            "Culture Day", "Labor Thanksgiving Day", "Spring Festival", "Qingming Festival",
            "Dragon Boat Festival", "Mid-Autumn Festival", "National Day", "Easter Monday",
            "Victory in Europe Day", "Ascension Day", "Whit Monday", "Bastille Day",
            "Assumption of Mary", "All Saints' Day", "Armistice Day",
            "Benito Juárez's Birthday", "Maundy Thursday", "Revolution Day", "Epiphany",
            "Hispanic Day", "Easter Sunday", "Whit Sunday", "Corpus Christi", "German Unity Day", "White Day", "Valentine's Day", "Halloween",
            "Delete selected entry?", "Entry text:"
    );
        manualTranslations = new HashMap<>();
        initializeManualTranslations();
        initializePolishTranslations();
    }

    private void initializeManualTranslations() {
        // Manually curated translations for specific terms and holidays

        Map<String, String> addEntryTranslations = new HashMap<>();
        addEntryTranslations.put("es", "Agregar entrada");
        addEntryTranslations.put("fr", "Ajouter une entrée");
        addEntryTranslations.put("de", "Eintrag hinzufügen");
        addEntryTranslations.put("it", "Aggiungi voce");
        addEntryTranslations.put("ko", "항목 추가");
        addEntryTranslations.put("pl", "Dodaj wpis");
        addEntryTranslations.put("zh", "添加条目");
        addEntryTranslations.put("zh-TW", "新增條目");
        addEntryTranslations.put("ja", "エントリを追加");
        addEntryTranslations.put("ar", "إضافة إدخال");
        manualTranslations.put("Add Entry", addEntryTranslations);

        Map<String, String> deleteEntryTranslations = new HashMap<>();
        deleteEntryTranslations.put("es", "Eliminar entrada");
        deleteEntryTranslations.put("fr", "Supprimer l’entrée");
        deleteEntryTranslations.put("de", "Eintrag löschen");
        deleteEntryTranslations.put("it", "Elimina voce");
        deleteEntryTranslations.put("ko", "항목 삭제");
        deleteEntryTranslations.put("pl", "Usuń wpis");
        deleteEntryTranslations.put("zh", "删除条目");
        deleteEntryTranslations.put("zh-TW", "刪除條目");
        deleteEntryTranslations.put("ja", "エントリを削除");
        deleteEntryTranslations.put("ar", "حذف الإدخال");
        manualTranslations.put("Delete Entry", deleteEntryTranslations);

        Map<String, String> editEntryTranslations = new HashMap<>();
        editEntryTranslations.put("es", "Editar entrada");
        editEntryTranslations.put("fr", "Modifier l’entrée");
        editEntryTranslations.put("de", "Eintrag bearbeiten");
        editEntryTranslations.put("it", "Modifica voce");
        editEntryTranslations.put("ko", "항목 편집");
        editEntryTranslations.put("pl", "Edytuj wpis");
        editEntryTranslations.put("zh", "编辑条目");
        editEntryTranslations.put("zh-TW", "編輯條目");
        editEntryTranslations.put("ja", "エントリを編集");
        editEntryTranslations.put("ar", "تحرير الإدخال");
        manualTranslations.put("Edit Entry", editEntryTranslations);

        Map<String, String> bulletJournalTranslations = new HashMap<>();
        bulletJournalTranslations.put("es", "Diario de viñetas");
        bulletJournalTranslations.put("fr", "Journal Bullet");
        bulletJournalTranslations.put("de", "Bullet Journal");
        bulletJournalTranslations.put("it", "Bullet Journal");
        bulletJournalTranslations.put("ar", "مفكرة النقاط");
        bulletJournalTranslations.put("ko", "불렛 저널");
        bulletJournalTranslations.put("pl", "Bullet Journal");
        bulletJournalTranslations.put("zh", "子弹日记");
        bulletJournalTranslations.put("zh-TW", "子彈日記");
        bulletJournalTranslations.put("ja", "バレットジャーナル");
        manualTranslations.put("Bullet Journal", bulletJournalTranslations);

        Map<String, String> settingsTranslations = new HashMap<>();
        settingsTranslations.put("es", "Configuración");
        settingsTranslations.put("fr", "Paramètres");
        settingsTranslations.put("de", "Einstellungen");
        settingsTranslations.put("it", "Impostazioni");
        settingsTranslations.put("ko", "설정");
        settingsTranslations.put("pl", "Ustawienia");
        settingsTranslations.put("zh", "设置");
        settingsTranslations.put("zh-TW", "設定");
        settingsTranslations.put("ja", "設定");
        settingsTranslations.put("ar", "الإعدادات");
        manualTranslations.put("Settings", settingsTranslations);

        Map<String, String> brightnessModeTranslations = new HashMap<>();
        brightnessModeTranslations.put("es", "Modo de brillo:");
        brightnessModeTranslations.put("fr", "Mode de luminosité :");
        brightnessModeTranslations.put("de", "Helligkeitsmodus:");
        brightnessModeTranslations.put("it", "Modalità luminosità:");
        brightnessModeTranslations.put("ko", "밝기 모드:");
        brightnessModeTranslations.put("pl", "Tryb jasności:");
        brightnessModeTranslations.put("zh", "亮度模式：");
        brightnessModeTranslations.put("zh-TW", "亮度模式：");
        brightnessModeTranslations.put("ja", "明るさモード:");
        brightnessModeTranslations.put("ar", "وضع السطوع:");
        manualTranslations.put("Brightness mode:", brightnessModeTranslations);

        Map<String, String> darkTranslations = new HashMap<>();
        darkTranslations.put("es", "Oscuro");
        darkTranslations.put("fr", "Sombre");
        darkTranslations.put("de", "Dunkel");
        darkTranslations.put("it", "Scuro");
        darkTranslations.put("ko", "어둡게");
        darkTranslations.put("pl", "Ciemny");
        darkTranslations.put("zh", "深色");
        darkTranslations.put("zh-TW", "深色");
        darkTranslations.put("ja", "ダーク");
        darkTranslations.put("ar", "داكن");
        manualTranslations.put("Dark", darkTranslations);

        Map<String, String> lightTranslations = new HashMap<>();
        lightTranslations.put("es", "Claro");
        lightTranslations.put("fr", "Clair");
        lightTranslations.put("de", "Hell");
        lightTranslations.put("it", "Chiaro");
        lightTranslations.put("ko", "밝게");
        lightTranslations.put("pl", "Jasny");
        lightTranslations.put("zh", "浅色");
        lightTranslations.put("zh-TW", "淺色");
        lightTranslations.put("ja", "ライト");
        lightTranslations.put("ar", "فاتح");
        manualTranslations.put("Light", lightTranslations);

        Map<String, String> saveScreenTranslations = new HashMap<>();
        saveScreenTranslations.put("es", "Guardar pantalla al cambiar tamaño");
        saveScreenTranslations.put("fr", "Enregistrer l'écran lors du redimensionnement");
        saveScreenTranslations.put("de", "Bildschirm beim Ändern der Größe speichern");
        saveScreenTranslations.put("it", "Salva schermo al ridimensionamento");
        saveScreenTranslations.put("ko", "크기 조정 시 화면 저장");
        saveScreenTranslations.put("pl", "Zapisz ekran po zmianie rozmiaru");
        saveScreenTranslations.put("zh", "调整大小时保存屏幕");
        saveScreenTranslations.put("zh-TW", "調整大小時保存畫面");
        saveScreenTranslations.put("ja", "サイズ変更時に画面を保存");
        saveScreenTranslations.put("ar", "حفظ الشاشة عند تغيير الحجم");
        manualTranslations.put("Save screen when resized", saveScreenTranslations);

        Map<String, String> savedSizeTranslations = new HashMap<>();
        savedSizeTranslations.put("es", "Tamaño guardado:");
        savedSizeTranslations.put("fr", "Taille enregistrée :");
        savedSizeTranslations.put("de", "Gespeicherte Größe:");
        savedSizeTranslations.put("it", "Dimensione salvata:");
        savedSizeTranslations.put("ko", "저장된 크기:");
        savedSizeTranslations.put("pl", "Zapisany rozmiar:");
        savedSizeTranslations.put("zh", "已保存大小：");
        savedSizeTranslations.put("zh-TW", "已保存大小：");
        savedSizeTranslations.put("ja", "保存されたサイズ:");
        savedSizeTranslations.put("ar", "الحجم المحفوظ:");
        manualTranslations.put("Saved size:", savedSizeTranslations);

        Map<String, String> notSetTranslations = new HashMap<>();
        notSetTranslations.put("es", "No establecido");
        notSetTranslations.put("fr", "Non défini");
        notSetTranslations.put("de", "Nicht festgelegt");
        notSetTranslations.put("it", "Non impostato");
        notSetTranslations.put("ko", "설정되지 않음");
        notSetTranslations.put("pl", "Nie ustawiono");
        notSetTranslations.put("zh", "未设置");
        notSetTranslations.put("zh-TW", "未設定");
        notSetTranslations.put("ja", "未設定");
        notSetTranslations.put("ar", "غير محدد");
        manualTranslations.put("Not set", notSetTranslations);

        Map<String, String> entryTextTranslations = new HashMap<>();
        entryTextTranslations.put("es", "نص الإدخال:");
        entryTextTranslations.put("fr", "Texte de l'entrée :");
        entryTextTranslations.put("de", "Eintragstext:");
        entryTextTranslations.put("it", "Testo della voce:");
        entryTextTranslations.put("ko", "نص الإدخال:");
        entryTextTranslations.put("pl", "Tekst wpisu:");
        entryTextTranslations.put("zh", "输入文本：");
        entryTextTranslations.put("zh-TW", "輸入文字：");
        entryTextTranslations.put("ja", "エントリのテキスト:");
        entryTextTranslations.put("ar", "نص الإدخال:");
        manualTranslations.put("Entry text:", entryTextTranslations);

        Map<String, String> deleteSelectedEntryTranslations = new HashMap<>();
        deleteSelectedEntryTranslations.put("es", "¿Eliminar الإدخال المحدد؟");
        deleteSelectedEntryTranslations.put("fr", "Supprimer l'entrée sélectionnée ?");
        deleteSelectedEntryTranslations.put("de", "Ausgewählten Eintrag löschen? ");
        deleteSelectedEntryTranslations.put("it", "Eliminare la voce selezionata?");
        deleteSelectedEntryTranslations.put("ko", "선택한 항목을 삭제하시겠습니까?");
        deleteSelectedEntryTranslations.put("pl", "Czy usunąć zaznaczony wpis?");
        deleteSelectedEntryTranslations.put("zh", "是否删除所选条目？");
        deleteSelectedEntryTranslations.put("zh-TW", "是否刪除所選條目？");
        deleteSelectedEntryTranslations.put("ja", "選択したエントリを削除しますか？");
        deleteSelectedEntryTranslations.put("ar", "هل تريد حذف الإدخال المحدد؟");
        manualTranslations.put("Delete selected entry?", deleteSelectedEntryTranslations);

        // Country
        Map<String, String> countryTranslations = new HashMap<>();
        countryTranslations.put("es", "País:");
        countryTranslations.put("fr", "Pays :");
        countryTranslations.put("de", "Land:");
        countryTranslations.put("it", "Paese:");
        countryTranslations.put("ko", "국가:");
        countryTranslations.put("pl", "Kraj:");
        countryTranslations.put("zh", "国家：");
        countryTranslations.put("zh-TW", "國家：");
        countryTranslations.put("ja", "国:");
        countryTranslations.put("ar", "البلد:");
        manualTranslations.put("Country:", countryTranslations);

        // Seollal
        Map<String, String> seollalTranslations = new HashMap<>();
        seollalTranslations.put("es", "Seollal");
        seollalTranslations.put("fr", "Seollal");
        seollalTranslations.put("de", "Seollal");
        seollalTranslations.put("it", "Seollal");
        seollalTranslations.put("ko", "설날");
        seollalTranslations.put("zh", "春节");
        seollalTranslations.put("zh-TW", "春節");
        seollalTranslations.put("ja", "ソルラル");
        seollalTranslations.put("ar", "سولال");
        manualTranslations.put("Seollal", seollalTranslations);

        // White Day
        Map<String, String> whiteDayTranslations = new HashMap<>();
        whiteDayTranslations.put("es", "Día Blanco");
        whiteDayTranslations.put("fr", "Jour Blanc");
        whiteDayTranslations.put("de", "Whiteday");
        whiteDayTranslations.put("it", "Giorno Bianco");
        whiteDayTranslations.put("ko", "화이트 데이");
        whiteDayTranslations.put("zh", "白色情人节");
        whiteDayTranslations.put("zh-TW", "白色情人節");
        whiteDayTranslations.put("ja", "ホワイトデー");
        whiteDayTranslations.put("ar", "يوم الأبيض");
        manualTranslations.put("White Day", whiteDayTranslations);

        // Seollal Eve
        Map<String, String> seollalEveTranslations = new HashMap<>();
        seollalEveTranslations.put("es", "Víspera de Seollal");
        seollalEveTranslations.put("fr", "Veille de Seollal");
        seollalEveTranslations.put("de", "Seollal-Vorabend");
        seollalEveTranslations.put("it", "Vigilia di Seollal");
        seollalEveTranslations.put("ko", "설날 전날");
        seollalEveTranslations.put("zh", "春节前夕");
        seollalEveTranslations.put("zh-TW", "春節前夕");
        seollalEveTranslations.put("ja", "ソルラル前夜");
        seollalEveTranslations.put("ar", "ليلة سولال");
        manualTranslations.put("Seollal Eve", seollalEveTranslations);

        // Seollal Holiday
        Map<String, String> seollalHolidayTranslations = new HashMap<>();
        seollalHolidayTranslations.put("es", "Día de Seollal");
        seollalHolidayTranslations.put("fr", "Jour de Seollal");
        seollalHolidayTranslations.put("de", "Seollal-Feiertag");
        seollalHolidayTranslations.put("it", "Giorno di Seollal");
        seollalHolidayTranslations.put("ko", "설날 당일");
        seollalHolidayTranslations.put("zh", "春节");
        seollalHolidayTranslations.put("zh-TW", "春節");
        seollalHolidayTranslations.put("ja", "ソルラル当日");
        seollalHolidayTranslations.put("ar", "عطلة سولال");
        manualTranslations.put("Seollal Holiday", seollalHolidayTranslations);

        // Additional Seollal Holiday
        Map<String, String> additionalSeollalHolidayTranslations = new HashMap<>();
        additionalSeollalHolidayTranslations.put("es", "Día adicional de Seollal");
        additionalSeollalHolidayTranslations.put("fr", "Jour supplémentaire de Seollal");
        additionalSeollalHolidayTranslations.put("de", "Zusätzlicher Seollal-Feiertag");
        additionalSeollalHolidayTranslations.put("it", "Giorno aggiuntivo di Seollal");
        additionalSeollalHolidayTranslations.put("ko", "추가 설날 휴일");
        additionalSeollalHolidayTranslations.put("zh", "额外的春节");
        additionalSeollalHolidayTranslations.put("zh-TW", "額外的春節");
        additionalSeollalHolidayTranslations.put("ja", "追加のソルラル休日");
        additionalSeollalHolidayTranslations.put("ar", "عطلة سولال إضافية");
        manualTranslations.put("Additional Seollal Holiday", additionalSeollalHolidayTranslations);

        // Chuseok Eve
        Map<String, String> chuseokEveTranslations = new HashMap<>();
        chuseokEveTranslations.put("es", "Víspera de Chuseok");
        chuseokEveTranslations.put("fr", "Veille de Chuseok");
        chuseokEveTranslations.put("de", "Chuseok-Vorabend");
        chuseokEveTranslations.put("it", "Vigilia di Chuseok");
        chuseokEveTranslations.put("ko", "추석 전날");
        chuseokEveTranslations.put("zh", "中秋节前夕");
        chuseokEveTranslations.put("zh-TW", "中秋節前夕");
        chuseokEveTranslations.put("ja", "チュソク前夜");
        chuseokEveTranslations.put("ar", "ليلة تشوسوك");
        manualTranslations.put("Chuseok Eve", chuseokEveTranslations);

        // Chuseok Holiday
        Map<String, String> chuseokHolidayTranslations = new HashMap<>();
        chuseokHolidayTranslations.put("es", "Día de Chuseok");
        chuseokHolidayTranslations.put("fr", "Jour de Chuseok");
        chuseokHolidayTranslations.put("de", "Chuseok-Feiertag");
        chuseokHolidayTranslations.put("it", "Giorno di Chuseok");
        chuseokHolidayTranslations.put("ko", "추석 당일");
        chuseokHolidayTranslations.put("zh", "中秋节");
        chuseokHolidayTranslations.put("zh-TW", "中秋節");
        chuseokHolidayTranslations.put("ja", "チュソク当日");
        chuseokHolidayTranslations.put("ar", "عطلة تشوسوك");
        manualTranslations.put("Chuseok Holiday", chuseokHolidayTranslations);

        // Remembrance Day
        Map<String, String> remembranceDayTranslations = new HashMap<>();
        remembranceDayTranslations.put("es", "Día de los Caídos");
        remembranceDayTranslations.put("fr", "Jour du Souvenir");
        remembranceDayTranslations.put("de", "Gedenktag");
        remembranceDayTranslations.put("it", "Giorno della Memoria");
        remembranceDayTranslations.put("ko", "현충일");
        remembranceDayTranslations.put("zh", "国殇日");
        remembranceDayTranslations.put("zh-TW", "國殤日");
        remembranceDayTranslations.put("ja", "追悼の日");
        remembranceDayTranslations.put("ar", "يوم الذكرى");
        manualTranslations.put("Remembrance Day", remembranceDayTranslations);

        // New Year's Day
        Map<String, String> newYearsDayTranslations = new HashMap<>();
        newYearsDayTranslations.put("es", "Año Nuevo");
        newYearsDayTranslations.put("fr", "Nouvel An");
        newYearsDayTranslations.put("de", "Neujahr");
        newYearsDayTranslations.put("it", "Capodanno");
        newYearsDayTranslations.put("ko", "새해");
        newYearsDayTranslations.put("zh", "新年");
        newYearsDayTranslations.put("zh-TW", "新年");
        newYearsDayTranslations.put("ja", "元日");
        newYearsDayTranslations.put("ar", "يوم رأس السنة");
        manualTranslations.put("New Year's Day", newYearsDayTranslations);

        // Family Day
        Map<String, String> familyDayTranslations = new HashMap<>();
        familyDayTranslations.put("es", "Día de la Familia");
        familyDayTranslations.put("fr", "Fête de la Famille");
        familyDayTranslations.put("de", "Familientag");
        familyDayTranslations.put("it", "Giornata della Famiglia");
        familyDayTranslations.put("ko", "가족의 날");
        familyDayTranslations.put("zh", "家庭日");
        familyDayTranslations.put("zh-TW", "家庭日");
        familyDayTranslations.put("ja", "家族の日");
        familyDayTranslations.put("ar", "يوم العائلة");
        manualTranslations.put("Family Day", familyDayTranslations);

        // Good Friday
        Map<String, String> goodFridayTranslations = new HashMap<>();
        goodFridayTranslations.put("es", "Viernes Santo");
        goodFridayTranslations.put("fr", "Vendredi Saint");
        goodFridayTranslations.put("de", "Karfreitag");
        goodFridayTranslations.put("it", "Venerdì Santo");
        goodFridayTranslations.put("ko", "성금요일");
        goodFridayTranslations.put("zh", "耶稣受难日");
        goodFridayTranslations.put("zh-TW", "耶穌受難日");
        goodFridayTranslations.put("ja", "聖金曜日");
        goodFridayTranslations.put("ar", "الجمعة العظيمة");
        manualTranslations.put("Good Friday", goodFridayTranslations);

        // Victoria Day
        Map<String, String> victoriaDayTranslations = new HashMap<>();
        victoriaDayTranslations.put("es", "Día de Victoria");
        victoriaDayTranslations.put("fr", "Fête de la Reine");
        victoriaDayTranslations.put("de", "Victoria-Tag");
        victoriaDayTranslations.put("it", "Giorno di Victoria");
        victoriaDayTranslations.put("ko", "빅토리아 데이");
        victoriaDayTranslations.put("zh", "维多利亚日");
        victoriaDayTranslations.put("zh-TW", "維多利亞日");
        victoriaDayTranslations.put("ja", "ビクトリアデー");
        victoriaDayTranslations.put("ar", "يوم فيكتوريا");
        manualTranslations.put("Victoria Day", victoriaDayTranslations);

        // Canada Day
        Map<String, String> canadaDayTranslations = new HashMap<>();
        canadaDayTranslations.put("es", "Día de Canadá");
        canadaDayTranslations.put("fr", "Fête du Canada");
        canadaDayTranslations.put("de", "Kanada-Tag");
        canadaDayTranslations.put("it", "Giorno del Canada");
        canadaDayTranslations.put("ko", "캐나다 데이");
        canadaDayTranslations.put("zh", "加拿大日");
        canadaDayTranslations.put("zh-TW", "加拿大日");
        canadaDayTranslations.put("ja", "カナダデー");
        canadaDayTranslations.put("ar", "اليوم الوطني الكندي");
        manualTranslations.put("Canada Day", canadaDayTranslations);

        // Civic Holiday
        Map<String, String> civicHolidayTranslations = new HashMap<>();
        civicHolidayTranslations.put("es", "Día Cívico");
        civicHolidayTranslations.put("fr", "Fête civique");
        civicHolidayTranslations.put("de", "Civic Holiday");
        civicHolidayTranslations.put("it", "Festa civica");
        civicHolidayTranslations.put("ko", "시민의 날");
        civicHolidayTranslations.put("zh", "公民节");
        civicHolidayTranslations.put("zh-TW", "公民節");
        civicHolidayTranslations.put("ja", "シビックホリデー");
        civicHolidayTranslations.put("ar", "عطلة مدنية");
        manualTranslations.put("Civic Holiday", civicHolidayTranslations);

        // Labour Day
        Map<String, String> labourDayTranslations = new HashMap<>();
        labourDayTranslations.put("es", "Día del Trabajo");
        labourDayTranslations.put("fr", "Fête du Travail");
        labourDayTranslations.put("de", "Tag der Arbeit");
        labourDayTranslations.put("it", "Festa del Lavoro");
        labourDayTranslations.put("ko", "노동절");
        labourDayTranslations.put("zh", "劳动节");
        labourDayTranslations.put("zh-TW", "勞動節");
        labourDayTranslations.put("ja", "労働の日");
        labourDayTranslations.put("ar", "عيد العمال");
        manualTranslations.put("Labour Day", labourDayTranslations);

        // Thanksgiving
        Map<String, String> thanksgivingTranslations = new HashMap<>();
        thanksgivingTranslations.put("es", "Día de Acción de Gracias");
        thanksgivingTranslations.put("fr", "Action de grâce");
        thanksgivingTranslations.put("de", "Erntedankfest");
        thanksgivingTranslations.put("it", "Ringraziamento");
        thanksgivingTranslations.put("ko", "추수감사절");
        thanksgivingTranslations.put("zh", "感恩节");
        thanksgivingTranslations.put("zh-TW", "感恩節");
        thanksgivingTranslations.put("ja", "感謝祭");
        thanksgivingTranslations.put("ar", "عيد الشكر");
        manualTranslations.put("Thanksgiving", thanksgivingTranslations);

        // Christmas Day
        Map<String, String> christmasDayTranslations = new HashMap<>();
        christmasDayTranslations.put("es", "Navidad");
        christmasDayTranslations.put("fr", "Noël");
        christmasDayTranslations.put("de", "Weihnachten");
        christmasDayTranslations.put("it", "Natale");
        christmasDayTranslations.put("ko", "크리스마스");
        christmasDayTranslations.put("zh", "圣诞节");
        christmasDayTranslations.put("zh-TW", "聖誕節");
        christmasDayTranslations.put("ja", "クリスマス");
        christmasDayTranslations.put("ar", "يوم عيد الميلاد");
        manualTranslations.put("Christmas Day", christmasDayTranslations);

        // Boxing Day
        Map<String, String> boxingDayTranslations = new HashMap<>();
        boxingDayTranslations.put("es", "Día de San Esteban");
        boxingDayTranslations.put("fr", "Lendemain de Noël");
        boxingDayTranslations.put("de", "Zweiter Weihnachtstag");
        boxingDayTranslations.put("it", "Santo Stefano");
        boxingDayTranslations.put("ko", "박싱 데이");
        boxingDayTranslations.put("zh", "节礼日");
        boxingDayTranslations.put("zh-TW", "節禮日");
        boxingDayTranslations.put("ja", "ボクシングデー");
        boxingDayTranslations.put("ar", "يوم عيد الصناديق");
        manualTranslations.put("Boxing Day", boxingDayTranslations);

        // MLK Jr. Day
        Map<String, String> mlkJrDayTranslations = new HashMap<>();
        mlkJrDayTranslations.put("es", "Día de MLK Jr.");
        mlkJrDayTranslations.put("fr", "Jour de MLK Jr.");
        mlkJrDayTranslations.put("de", "MLK Jr. Tag");
        mlkJrDayTranslations.put("it", "Giorno di MLK Jr.");
        mlkJrDayTranslations.put("ko", "MLK Jr. 의 날");
        mlkJrDayTranslations.put("zh", "MLK Jr. 日");
        mlkJrDayTranslations.put("zh-TW", "MLK Jr. 日");
        mlkJrDayTranslations.put("ja", "MLK Jr. デー");
        mlkJrDayTranslations.put("ar", "يوم مارتن لوثر كنج الابن");
        manualTranslations.put("MLK Jr. Day", mlkJrDayTranslations);

        // Presidents' Day
        Map<String, String> presidentsDayTranslations = new HashMap<>();
        presidentsDayTranslations.put("es", "Día de los Presidentes");
        presidentsDayTranslations.put("fr", "Jour des Présidents");
        presidentsDayTranslations.put("de", "Präsidententag");
        presidentsDayTranslations.put("it", "Giorno dei Presidenti");
        presidentsDayTranslations.put("ko", "대통령일");
        presidentsDayTranslations.put("zh", "总统日");
        presidentsDayTranslations.put("zh-TW", "總統日");
        presidentsDayTranslations.put("ja", "大統領の日");
        presidentsDayTranslations.put("ar", "يوم الرؤساء");
        manualTranslations.put("Presidents' Day", presidentsDayTranslations);

        // Memorial Day
        Map<String, String> memorialDayTranslations = new HashMap<>();
        memorialDayTranslations.put("es", "Día del Memorial");
        memorialDayTranslations.put("fr", "Jour du Mémorial");
        memorialDayTranslations.put("de", "Gedenktag");
        memorialDayTranslations.put("it", "Giorno del Memento");
        memorialDayTranslations.put("ko", "기념일");
        memorialDayTranslations.put("zh", "纪念日");
        memorialDayTranslations.put("zh-TW", "紀念日");
        memorialDayTranslations.put("ja", "記念日");
        memorialDayTranslations.put("ar", "يوم الذكرى");
        manualTranslations.put("Memorial Day", memorialDayTranslations);

        // Juneteenth
        Map<String, String> juneteenthTranslations = new HashMap<>();
        juneteenthTranslations.put("es", "Día de Juneteenth");
        juneteenthTranslations.put("fr", "Jour de Juneteenth");
        juneteenthTranslations.put("de", "Juneteenth Tag");
        juneteenthTranslations.put("it", "Giorno di Juneteenth");
        juneteenthTranslations.put("ko", "준테nth의 날");
        juneteenthTranslations.put("zh", "朱内斯节");
        juneteenthTranslations.put("zh-TW", "朱內斯節");
        juneteenthTranslations.put("ja", "ジュネットスデー");
        juneteenthTranslations.put("ar", "اليوم الوطني للحرية");
        manualTranslations.put("Juneteenth", juneteenthTranslations);

        // Independence Day
        Map<String, String> independenceDayTranslations = new HashMap<>();
        independenceDayTranslations.put("es", "Día de la Independencia");
        independenceDayTranslations.put("fr", "Jour de l'Indépendance");
        independenceDayTranslations.put("de", "Unabhängigkeitstag");
        independenceDayTranslations.put("it", "Giorno dell'Indipendenza");
        independenceDayTranslations.put("ko", "독립일");
        independenceDayTranslations.put("zh", "独立日");
        independenceDayTranslations.put("zh-TW", "獨立日");
        independenceDayTranslations.put("ja", "独立の日");
        independenceDayTranslations.put("ar", "يوم الاستقلال");
        manualTranslations.put("Independence Day", independenceDayTranslations);

        // Labor Day
        Map<String, String> laborDayTranslations = new HashMap<>();
        laborDayTranslations.put("es", "Día del Trabajo");
        laborDayTranslations.put("fr", "Jour du Travail");
        laborDayTranslations.put("de", "Arbeitstag");
        laborDayTranslations.put("it", "Giorno del Lavoro");
        laborDayTranslations.put("ko", "로버의 날");
        laborDayTranslations.put("zh", "劳动节");
        laborDayTranslations.put("zh-TW", "勞動節");
        laborDayTranslations.put("ja", "労働節");
        laborDayTranslations.put("ar", "عيد العمال");
        manualTranslations.put("Labor Day", laborDayTranslations);

        // Christmas
        Map<String, String> christmasTranslations = new HashMap<>();
        christmasTranslations.put("es", "Navidad");
        christmasTranslations.put("fr", "Noël");
        christmasTranslations.put("de", "Weihnachten");
        christmasTranslations.put("it", "Natale");
        christmasTranslations.put("ko", "크리스마스");
        christmasTranslations.put("zh", "圣诞节");
        christmasTranslations.put("zh-TW", "聖誕節");
        christmasTranslations.put("ja", "クリスマス");
        christmasTranslations.put("ar", "عيد الميلاد");
        manualTranslations.put("Christmas", christmasTranslations);

        // Independence Movement Day
        Map<String, String> independenceMovementDayTranslations = new HashMap<>();
        independenceMovementDayTranslations.put("es", "Día del Movimiento de la Independencia");
        independenceMovementDayTranslations.put("fr", "Jour du Mouvement de l'Indépendance");
        independenceMovementDayTranslations.put("de", "Tag des Unabhängigkeitbewegung");
        independenceMovementDayTranslations.put("it", "Giorno del Movimento dell'Indipendenza");
        independenceMovementDayTranslations.put("ko", "독립 운동의 날");
        independenceMovementDayTranslations.put("zh", "独立运动日");
        independenceMovementDayTranslations.put("zh-TW", "獨立運動日");
        independenceMovementDayTranslations.put("ja", "独立運動の日");
        independenceMovementDayTranslations.put("ar", "يوم حركة الاستقلال");
        manualTranslations.put("Independence Movement Day", independenceMovementDayTranslations);

        // Parliamentary Election Day
        Map<String, String> parliamentaryElectionDayTranslations = new HashMap<>();
        parliamentaryElectionDayTranslations.put("es", "Día de la Elección Parlamentaria");
        parliamentaryElectionDayTranslations.put("fr", "Jour de l'Élection Parlementaire");
        parliamentaryElectionDayTranslations.put("de", "Tag der Parlamentarischen Wahl");
        parliamentaryElectionDayTranslations.put("it", "Giorno dell'Elezioni Parlamentari");
        parliamentaryElectionDayTranslations.put("ko", "의회 선거의 날");
        parliamentaryElectionDayTranslations.put("zh", "议会选举日");
        parliamentaryElectionDayTranslations.put("zh-TW", "議會選舉日");
        parliamentaryElectionDayTranslations.put("ja", "議会選挙の日");
        parliamentaryElectionDayTranslations.put("ar", "يوم الانتخابات البرلمانية");
        manualTranslations.put("Parliamentary Election Day", parliamentaryElectionDayTranslations);

        // Children's Day
        Map<String, String> childrensDayTranslations = new HashMap<>();
        childrensDayTranslations.put("es", "Día de los Niños");
        childrensDayTranslations.put("fr", "Jour des Enfants");
        childrensDayTranslations.put("de", "Kindertag");
        childrensDayTranslations.put("it", "Giorno dei Bambini");
        childrensDayTranslations.put("ko", "어린이날");
        childrensDayTranslations.put("zh", "儿童节");
        childrensDayTranslations.put("zh-TW", "兒童節");
        childrensDayTranslations.put("ja", "児童節");
        childrensDayTranslations.put("ar", "يوم الطفل");
        manualTranslations.put("Children's Day", childrensDayTranslations);

        // Buddha's Birthday
        Map<String, String> buddhasBirthdayTranslations = new HashMap<>();
        buddhasBirthdayTranslations.put("es", "Cumpleaños de Buda");
        buddhasBirthdayTranslations.put("fr", "Anniversaire de Bouddha");
        buddhasBirthdayTranslations.put("de", "Buddha Geburtstag");
        buddhasBirthdayTranslations.put("it", "Compleanno di Buddha");
        buddhasBirthdayTranslations.put("ko", "부처님 오신 날");
        buddhasBirthdayTranslations.put("zh", "佛诞节");
        buddhasBirthdayTranslations.put("zh-TW", "佛誕節");
        buddhasBirthdayTranslations.put("ja", "仏生日");
        buddhasBirthdayTranslations.put("ar", "عيد ميلاد بوذا");
        manualTranslations.put("Buddha's Birthday", buddhasBirthdayTranslations);

        // Liberation Day
        Map<String, String> liberationDayTranslations = new HashMap<>();
        liberationDayTranslations.put("es", "Día de la Liberación");
        liberationDayTranslations.put("fr", "Jour de la Libération");
        liberationDayTranslations.put("de", "Tag der Befreiung");
        liberationDayTranslations.put("it", "Giorno della Liberazione");
        liberationDayTranslations.put("ko", "해방의 날");
        liberationDayTranslations.put("zh", "解放日");
        liberationDayTranslations.put("zh-TW", "解放日");
        liberationDayTranslations.put("ja", "解放の日");
        liberationDayTranslations.put("ar", "يوم التحرير");
        manualTranslations.put("Liberation Day", liberationDayTranslations);

        // National Foundation Day
        Map<String, String> nationalFoundationDayTranslations = new HashMap<>();
        nationalFoundationDayTranslations.put("es", "Día de la Fundación Nacional");
        nationalFoundationDayTranslations.put("fr", "Jour de la Fondation Nationale");
        nationalFoundationDayTranslations.put("de", "Tag der Nationalen Grundlegung");
        nationalFoundationDayTranslations.put("it", "Giorno della Fondazione Nazionale");
        nationalFoundationDayTranslations.put("ko", "국가 설립의 날");
        nationalFoundationDayTranslations.put("zh", "国庆节");
        nationalFoundationDayTranslations.put("zh-TW", "國慶節");
        nationalFoundationDayTranslations.put("ja", "建国記念の日");
        nationalFoundationDayTranslations.put("ar", "يوم التأسيس الوطني");
        manualTranslations.put("National Foundation Day", nationalFoundationDayTranslations);

        // Hangeul Day
        Map<String, String> hangeulDayTranslations = new HashMap<>();
        hangeulDayTranslations.put("es", "Día del Hangeul");
        hangeulDayTranslations.put("fr", "Jour du Hangeul");
        hangeulDayTranslations.put("de", "Tag des Hangeuls");
        hangeulDayTranslations.put("it", "Giorno del Hangeul");
        hangeulDayTranslations.put("ko", "한글날");
        hangeulDayTranslations.put("zh", "韩文日");
        hangeulDayTranslations.put("zh-TW", "韓文日");
        hangeulDayTranslations.put("ja", "ハングルの日");
        hangeulDayTranslations.put("ar", "يوم الهانغول");
        manualTranslations.put("Hangeul Day", hangeulDayTranslations);

        // Coming of Age Day
        Map<String, String> comingOfAgeDayTranslations = new HashMap<>();
        comingOfAgeDayTranslations.put("es", "Día de la Mayoría de Edad");
        comingOfAgeDayTranslations.put("fr", "Jour de la Majorité");
        comingOfAgeDayTranslations.put("de", "Tag der Volljährigkeit");
        comingOfAgeDayTranslations.put("it", "Giorno della Maggiore Età");
        comingOfAgeDayTranslations.put("ko", "성년의 날");
        comingOfAgeDayTranslations.put("zh", "成年节");
        comingOfAgeDayTranslations.put("zh-TW", "成年節");
        comingOfAgeDayTranslations.put("ja", "成人の日");
        comingOfAgeDayTranslations.put("ar", "يوم بلوغ السن القانوني");
        manualTranslations.put("Coming of Age Day", comingOfAgeDayTranslations);

        // Foundation Day
        Map<String, String> foundationDayTranslations = new HashMap<>();
        foundationDayTranslations.put("es", "Día de la Fundación");
        foundationDayTranslations.put("fr", "Jour de la Fondation");
        foundationDayTranslations.put("de", "Tag der Grundlegung");
        foundationDayTranslations.put("it", "Giorno della Fondazione");
        foundationDayTranslations.put("ko", "설립의 날");
        foundationDayTranslations.put("zh", "建校日");
        foundationDayTranslations.put("zh-TW", "建校日");
        foundationDayTranslations.put("ja", "創立記念の日");
        foundationDayTranslations.put("ar", "يوم التأسيس");
        manualTranslations.put("Foundation Day", foundationDayTranslations);

        // Vernal Equinox Day
        Map<String, String> vernalEquinoxDayTranslations = new HashMap<>();
        vernalEquinoxDayTranslations.put("es", "Día del Equinoccio de Primavera");
        vernalEquinoxDayTranslations.put("fr", "Jour de l'Équinoxe de Printemps");
        vernalEquinoxDayTranslations.put("de", "Tag des Frühlingsequinokx");
        vernalEquinoxDayTranslations.put("it", "Giorno dell'Equinozio di Primavera");
        vernalEquinoxDayTranslations.put("ko", "춘분의 날");
        vernalEquinoxDayTranslations.put("zh", "春分日");
        vernalEquinoxDayTranslations.put("zh-TW", "春分日");
        vernalEquinoxDayTranslations.put("ja", "春分の日");
        vernalEquinoxDayTranslations.put("ar", "يوم الاعتدال الربيعي");
        manualTranslations.put("Vernal Equinox Day", vernalEquinoxDayTranslations);

        // Showa Day
        Map<String, String> showaDayTranslations = new HashMap<>();
        showaDayTranslations.put("es", "Día de Showa");
        showaDayTranslations.put("fr", "Jour de Showa");
        showaDayTranslations.put("de", "Tag von Showa");
        showaDayTranslations.put("it", "Giorno di Showa");
        showaDayTranslations.put("ko", "쇼와의 날");
        showaDayTranslations.put("zh", "昭和日");
        showaDayTranslations.put("zh-TW", "昭和日");
        showaDayTranslations.put("ja", "昭和の日");
        showaDayTranslations.put("ar", "يوم شووا");
        manualTranslations.put("Showa Day", showaDayTranslations);

        // Constitution Day
        Map<String, String> constitutionDayTranslations = new HashMap<>();
        constitutionDayTranslations.put("es", "Día de la Constitución");
        constitutionDayTranslations.put("fr", "Jour de la Constitution");
        constitutionDayTranslations.put("de", "Tag der Verfassung");
        constitutionDayTranslations.put("it", "Giorno della Costituzione");
        constitutionDayTranslations.put("ko", "헌법의 날");
        constitutionDayTranslations.put("zh", "宪法日");
        constitutionDayTranslations.put("zh-TW", "憲法日");
        constitutionDayTranslations.put("ja", "憲法の日");
        constitutionDayTranslations.put("ar", "يوم الدستور");
        manualTranslations.put("Constitution Day", constitutionDayTranslations);

        // Greenery Day
        Map<String, String> greeneryDayTranslations = new HashMap<>();
        greeneryDayTranslations.put("es", "Día de la Vegetación");
        greeneryDayTranslations.put("fr", "Jour de la Végétation");
        greeneryDayTranslations.put("de", "Tag der Vegetation");
        greeneryDayTranslations.put("it", "Giorno della Vegetazione");
        greeneryDayTranslations.put("ko", "녹색의 날");
        greeneryDayTranslations.put("zh", "绿化日");
        greeneryDayTranslations.put("zh-TW", "綠化日");
        greeneryDayTranslations.put("ja", "緑の日");
        greeneryDayTranslations.put("ar", "يوم الطبيعة");
        manualTranslations.put("Greenery Day", greeneryDayTranslations);

        // Marine Day
        Map<String, String> marineDayTranslations = new HashMap<>();
        marineDayTranslations.put("es", "Día del Mar");
        marineDayTranslations.put("fr", "Jour de la Mer");
        marineDayTranslations.put("de", "Tag des Meeres");
        marineDayTranslations.put("it", "Giorno del Mare");
        marineDayTranslations.put("ko", "해양의 날");
        marineDayTranslations.put("zh", "海洋日");
        marineDayTranslations.put("zh-TW", "海洋日");
        marineDayTranslations.put("ja", "海の日");
        marineDayTranslations.put("ar", "يوم البحار");
        manualTranslations.put("Marine Day", marineDayTranslations);

        // Mountain Day
        Map<String, String> mountainDayTranslations = new HashMap<>();
        mountainDayTranslations.put("es", "Día de la Montaña");
        mountainDayTranslations.put("fr", "Jour de la Montagne");
        mountainDayTranslations.put("de", "Tag der Berge");
        mountainDayTranslations.put("it", "Giorno delle Montagne");
        mountainDayTranslations.put("ko", "산의 날");
        mountainDayTranslations.put("zh", "山之日");
        mountainDayTranslations.put("zh-TW", "山之日");
        mountainDayTranslations.put("ja", "山の日");
        mountainDayTranslations.put("ar", "يوم الجبل");
        manualTranslations.put("Mountain Day", mountainDayTranslations);

        // Respect for the Aged Day
        Map<String, String> respectForAgedDayTranslations = new HashMap<>();
        respectForAgedDayTranslations.put("es", "Día del Respeto a los Mayores");
        respectForAgedDayTranslations.put("fr", "Jour du Respect pour les Aînés");
        respectForAgedDayTranslations.put("de", "Tag des Respekts vor den Älteren");
        respectForAgedDayTranslations.put("it", "Giorno del Rispetto per gli Anziani");
        respectForAgedDayTranslations.put("ko", "노인 존경의 날");
        respectForAgedDayTranslations.put("zh", "敬老日");
        respectForAgedDayTranslations.put("zh-TW", "敬老日");
        respectForAgedDayTranslations.put("ja", "敬老の日");
        respectForAgedDayTranslations.put("ar", "يوم احترام المسنين");
        manualTranslations.put("Respect for the Aged Day", respectForAgedDayTranslations);

        // Autumnal Equinox Day
        Map<String, String> autumnalEquinoxDayTranslations = new HashMap<>();
        autumnalEquinoxDayTranslations.put("es", "Día del Equinoccio de Otoño");
        autumnalEquinoxDayTranslations.put("fr", "Jour de l'Équinoxe d'Automne");
        autumnalEquinoxDayTranslations.put("de", "Tag des Herbstgleichen");
        autumnalEquinoxDayTranslations.put("it", "Giorno dell'Equinozio d'Autunno");
        autumnalEquinoxDayTranslations.put("ko", "추석의 날");
        autumnalEquinoxDayTranslations.put("zh", "秋分日");
        autumnalEquinoxDayTranslations.put("zh-TW", "秋分日");
        autumnalEquinoxDayTranslations.put("ja", "秋分の日");
        autumnalEquinoxDayTranslations.put("ar", "يوم الاعتدال الخريفي");
        manualTranslations.put("Autumnal Equinox Day", autumnalEquinoxDayTranslations);

        // Sports Day
        Map<String, String> sportsDayTranslations = new HashMap<>();
        sportsDayTranslations.put("es", "Día del Deporte");
        sportsDayTranslations.put("fr", "Jour du Sport");
        sportsDayTranslations.put("de", "Tag des Sports");
        sportsDayTranslations.put("it", "Giorno dello Sport");
        sportsDayTranslations.put("ko", "스포츠의 날");
        sportsDayTranslations.put("zh", "体育日");
        sportsDayTranslations.put("zh-TW", "體育日");
        sportsDayTranslations.put("ja", "スポーツの日");
        sportsDayTranslations.put("ar", "يوم الرياضة");
        manualTranslations.put("Sports Day", sportsDayTranslations);

        // Culture Day
        Map<String, String> cultureDayTranslations = new HashMap<>();
        cultureDayTranslations.put("es", "Día de la Cultura");
        cultureDayTranslations.put("fr", "Jour de la Culture");
        cultureDayTranslations.put("de", "Tag der Kultur");
        cultureDayTranslations.put("it", "Giorno della Cultura");
        cultureDayTranslations.put("ko", "문화의 날");
        cultureDayTranslations.put("zh", "文化日");
        cultureDayTranslations.put("zh-TW", "文化日");
        cultureDayTranslations.put("ja", "文化の日");
        cultureDayTranslations.put("ar", "يوم الثقافة");
        manualTranslations.put("Culture Day", cultureDayTranslations);

        // Labor Thanksgiving Day
        Map<String, String> laborThanksgivingDayTranslations = new HashMap<>();
        laborThanksgivingDayTranslations.put("es", "Día de la Gratitud del Trabajo");
        laborThanksgivingDayTranslations.put("fr", "Jour de la Reconnaissance du Travail");
        laborThanksgivingDayTranslations.put("de", "Tag der Dankbarkeit für die Arbeit");
        laborThanksgivingDayTranslations.put("it", "Giorno della Riconoscenza del Lavoro");
        laborThanksgivingDayTranslations.put("ko", "노동 감사절");
        laborThanksgivingDayTranslations.put("zh", "劳动感恩节");
        laborThanksgivingDayTranslations.put("zh-TW", "勞動感恩節");
        laborThanksgivingDayTranslations.put("ja", "労働感謝の日");
        laborThanksgivingDayTranslations.put("ar", "عيد الشكر للعمل");
        manualTranslations.put("Labor Thanksgiving Day", laborThanksgivingDayTranslations);

        // Spring Festival
        Map<String, String> springFestivalTranslations = new HashMap<>();
        springFestivalTranslations.put("es", "Festival de la Primavera");
        springFestivalTranslations.put("fr", "Festival de Printemps");
        springFestivalTranslations.put("de", "Frühlingfest");
        springFestivalTranslations.put("it", "Festa della Primavera");
        springFestivalTranslations.put("ko", "춘절");
        springFestivalTranslations.put("zh", "春节");
        springFestivalTranslations.put("zh-TW", "春节");
        springFestivalTranslations.put("ja", "春節");
        springFestivalTranslations.put("ar", "مهرجان الربيع");
        manualTranslations.put("Spring Festival", springFestivalTranslations);


        // Qingming Festival
        Map<String, String> qingmingFestivalTranslations = new HashMap<>();
        qingmingFestivalTranslations.put("es", "Festival de Qingming");
        qingmingFestivalTranslations.put("fr", "Festival de Qingming");
        qingmingFestivalTranslations.put("de", "Festival von Qingming");
        qingmingFestivalTranslations.put("it", "Festa di Qingming");
        qingmingFestivalTranslations.put("ko", "청명절");
        qingmingFestivalTranslations.put("zh", "清明节");
        qingmingFestivalTranslations.put("zh-TW", "清明節");
        qingmingFestivalTranslations.put("ja", "清明節");
        qingmingFestivalTranslations.put("ar", "مهرجان تشينغمينغ");
        manualTranslations.put("Qingming Festival", qingmingFestivalTranslations);

        // Dragon Boat Festival
        Map<String, String> dragonBoatFestivalTranslations = new HashMap<>();
        dragonBoatFestivalTranslations.put("es", "Festival del Bote Dragón");
        dragonBoatFestivalTranslations.put("fr", "Festival du Bateau Dragon");
        dragonBoatFestivalTranslations.put("de", "Festival des Drachenbootes");
        dragonBoatFestivalTranslations.put("it", "Festa della Barca del Drago");
        dragonBoatFestivalTranslations.put("ko", "드래곤 보트 페스티벌");
        dragonBoatFestivalTranslations.put("zh", "龙舟节");
        dragonBoatFestivalTranslations.put("zh-TW", "龍舟節");
        dragonBoatFestivalTranslations.put("ja", "竜舟祭");
        dragonBoatFestivalTranslations.put("ar", "مهرجان قوارب التنين");
        manualTranslations.put("Dragon Boat Festival", dragonBoatFestivalTranslations);

        // Mid-Autumn Festival
        Map<String, String> midAutumnFestivalTranslations = new HashMap<>();
        midAutumnFestivalTranslations.put("es", "Festival de Medio Otoño");
        midAutumnFestivalTranslations.put("fr", "Festival de Mi-Autumn");
        midAutumnFestivalTranslations.put("de", "Festival der Mitte des Herbstes");
        midAutumnFestivalTranslations.put("it", "Festa dell'Autunno al Centro");
        midAutumnFestivalTranslations.put("ko", "중추절");
        midAutumnFestivalTranslations.put("zh", "中秋节");
        midAutumnFestivalTranslations.put("zh-TW", "中秋節");
        midAutumnFestivalTranslations.put("ja", "中 秋 節");
        midAutumnFestivalTranslations.put("ar", "مهرجان منتصف الخريف");
        manualTranslations.put("Mid-Autumn Festival", midAutumnFestivalTranslations);

        // National Day
        Map<String, String> nationalDayTranslations = new HashMap<>();
        nationalDayTranslations.put("es", "Día Nacional");
        nationalDayTranslations.put("fr", "Jour National");
        nationalDayTranslations.put("de", "Nationale Feier");
        nationalDayTranslations.put("it", "Giorno Nazionale");
        nationalDayTranslations.put("ko", "국경일");
        nationalDayTranslations.put("zh", "国庆节");
        nationalDayTranslations.put("zh-TW", "國慶節");
        nationalDayTranslations.put("ja", "国庆節");
        nationalDayTranslations.put("ar", "اليوم الوطني");
        manualTranslations.put("National Day", nationalDayTranslations);

        // Easter Monday
        Map<String, String> easterMondayTranslations = new HashMap<>();
        easterMondayTranslations.put("es", "Lunes de Pascua");
        easterMondayTranslations.put("fr", "Lundi de Pâques");
        easterMondayTranslations.put("de", "Montag nach Ostern");
        easterMondayTranslations.put("it", "Lunedì di Pasqua");
        easterMondayTranslations.put("ko", "부활절 월요일");
        easterMondayTranslations.put("zh", "复活节星期一");
        easterMondayTranslations.put("zh-TW", "復活節星期一");
        easterMondayTranslations.put("ja", "復活節月曜日");
        easterMondayTranslations.put("ar", "اثنين الفصح");
        manualTranslations.put("Easter Monday", easterMondayTranslations);

        // Ascension Day
        Map<String, String> ascensionDayTranslations = new HashMap<>();
        ascensionDayTranslations.put("es", "Día de la Ascensión");
        ascensionDayTranslations.put("fr", "Jour de l'Ascension");
        ascensionDayTranslations.put("de", "Christi Himmelfahrt");
        ascensionDayTranslations.put("it", "Giorno dell'Ascensione");
        ascensionDayTranslations.put("ko", "승천절");
        ascensionDayTranslations.put("zh", "耶稣升天节");
        ascensionDayTranslations.put("zh-TW", "耶穌升天節");
        ascensionDayTranslations.put("ja", "昇天の日");
        ascensionDayTranslations.put("ar", "عيد الصعود");
        manualTranslations.put("Ascension Day", ascensionDayTranslations);

        // Whit Monday
        Map<String, String> whitMondayTranslations = new HashMap<>();
        whitMondayTranslations.put("es", "Lunes de Pentecostés");
        whitMondayTranslations.put("fr", "Lundi de Pentecôte");
        whitMondayTranslations.put("de", "Montag nach Pfingsten");
        whitMondayTranslations.put("it", "Lunedì di Pentecoste");
        whitMondayTranslations.put("ko", "백성절 월요일");
        whitMondayTranslations.put("zh", "圣灵降临节星期一");
        whitMondayTranslations.put("zh-TW", "聖靈降臨節星期一");
        whitMondayTranslations.put("ja", "聖霊降臨節月曜日");
        whitMondayTranslations.put("ar", "اثنين العنصرة");
        manualTranslations.put("Whit Monday", whitMondayTranslations);

        // Bastille Day
        Map<String, String> bastilleDayTranslations = new HashMap<>();
        bastilleDayTranslations.put("es", "Día de la Bastilla");
        bastilleDayTranslations.put("fr", "Fête de la Bastille");
        bastilleDayTranslations.put("de", "Bastilliertag");
        bastilleDayTranslations.put("it", "Festa della Bastiglia");
        bastilleDayTranslations.put("ko", "바스티유 절");
        bastilleDayTranslations.put("zh", "巴士底日");
        bastilleDayTranslations.put("zh-TW", "巴士底日");
        bastilleDayTranslations.put("ja", "バスタイユの日");
        bastilleDayTranslations.put("ar", "يوم الباستيل");
        manualTranslations.put("Bastille Day", bastilleDayTranslations);

        // Victory in Europe Day
        Map<String, String> victoryInEuropeDayTranslations = new HashMap<>();
        victoryInEuropeDayTranslations.put("es", "Día de la Victoria en Europa");
        victoryInEuropeDayTranslations.put("fr", "Fête de la Victoire en Europe");
        victoryInEuropeDayTranslations.put("de", "Tag der Sieg in Europa");
        victoryInEuropeDayTranslations.put("it", "Festa della Vittoria in Europa");
        victoryInEuropeDayTranslations.put("ko", "유럽에서의 승리의 날");
        victoryInEuropeDayTranslations.put("zh", "欧洲胜利日");
        victoryInEuropeDayTranslations.put("zh-TW", "歐洲勝利日");
        victoryInEuropeDayTranslations.put("ja", "ヨーロッパ勝利の日");
        victoryInEuropeDayTranslations.put("ar", "يوم النصر في أوروبا");
        manualTranslations.put("Victory in Europe Day", victoryInEuropeDayTranslations);

        // Assumption of Mary
        Map<String, String> assumptionOfMaryTranslations = new HashMap<>();
        assumptionOfMaryTranslations.put("es", "Asunción de la Virgen");
        assumptionOfMaryTranslations.put("fr", "Assomption de Marie");
        assumptionOfMaryTranslations.put("de", "Himmelfahrt der Jungfrau Maria");
        assumptionOfMaryTranslations.put("it", "Assunzione di Maria");
        assumptionOfMaryTranslations.put("ko", "성모 승천절");
        assumptionOfMaryTranslations.put("zh", "圣母升天节");
        assumptionOfMaryTranslations.put("zh-TW", "聖母升天節");
        assumptionOfMaryTranslations.put("ja", "聖母昇天の日");
        assumptionOfMaryTranslations.put("ar", "عيد انتقال مريم");
        manualTranslations.put("Assumption of Mary", assumptionOfMaryTranslations);

        // All Saints' Day
        Map<String, String> allSaintsDayTranslations = new HashMap<>();
        allSaintsDayTranslations.put("es", "Día de todos los Santos");
        allSaintsDayTranslations.put("fr", "Tous les Saints");
        allSaintsDayTranslations.put("de", "Allerheiligen");
        allSaintsDayTranslations.put("it", "Tutti i Santi");
        allSaintsDayTranslations.put("ko", "모든 성인의 날");
        allSaintsDayTranslations.put("zh", "诸圣节");
        allSaintsDayTranslations.put("zh-TW", "諸聖節");
        allSaintsDayTranslations.put("ja", "すべての聖人の日");
        allSaintsDayTranslations.put("ar", "عيد جميع القديسين");
        manualTranslations.put("All Saints' Day", allSaintsDayTranslations);

        // Armistice Day
        Map<String, String> armisticeDayTranslations = new HashMap<>();
        armisticeDayTranslations.put("es", "Día de la Armisticio");
        armisticeDayTranslations.put("fr", "Jour de l'Armistice");
        armisticeDayTranslations.put("de", "Waffenstillstandstag");
        armisticeDayTranslations.put("it", "Giorno dell'Armistizio");
        armisticeDayTranslations.put("ko", "중지일");
        armisticeDayTranslations.put("zh", "停战日");
        armisticeDayTranslations.put("zh-TW", "停戰日");
        armisticeDayTranslations.put("ja", "停戦の日");
        armisticeDayTranslations.put("ar", "يوم الهدنة");
        manualTranslations.put("Armistice Day", armisticeDayTranslations);

        // Benito Juárez's Birthday
        Map<String, String> benitoJuarezBirthdayTranslations = new HashMap<>();
        benitoJuarezBirthdayTranslations.put("es", "Cumpleaños de Benito Juárez");
        benitoJuarezBirthdayTranslations.put("fr", "Anniversaire de Benito Juárez");
        benitoJuarezBirthdayTranslations.put("de", "Geburtstag von Benito Juárez");
        benitoJuarezBirthdayTranslations.put("it", "Compleanno di Benito Juárez");
        benitoJuarezBirthdayTranslations.put("ko", "베니토 훠레즈 생일");
        benitoJuarezBirthdayTranslations.put("zh", "贝尼托·胡亚雷斯生日");
        benitoJuarezBirthdayTranslations.put("zh-TW", "貝尼托·胡亞雷斯生日");
        benitoJuarezBirthdayTranslations.put("ja", "ベニート・フエレスの誕生日");
        benitoJuarezBirthdayTranslations.put("ar", "عيد ميلاد بينيتو خواريز");
        manualTranslations.put("Benito Juárez's Birthday", benitoJuarezBirthdayTranslations);

        // Maundy Thursday
        Map<String, String> maundyThursdayTranslations = new HashMap<>();
        maundyThursdayTranslations.put("es", "Jueves Santo");
        maundyThursdayTranslations.put("fr", "Jeudi Saint");
        maundyThursdayTranslations.put("de", "Karfreitag");
        maundyThursdayTranslations.put("it", "Giovedì Santo");
        maundyThursdayTranslations.put("ko", "성목요일");
        maundyThursdayTranslations.put("zh", "圣周四");
        maundyThursdayTranslations.put("zh-TW", "聖週四");
        maundyThursdayTranslations.put("ja", "聖木の日");
        maundyThursdayTranslations.put("ar", "خميس الغسل");
        manualTranslations.put("Maundy Thursday", maundyThursdayTranslations);

        // Revolution Day
        Map<String, String> revolutionDayTranslations = new HashMap<>();
        revolutionDayTranslations.put("es", "Día de la Revolución");
        revolutionDayTranslations.put("fr", "Jour de la Révolution");
        revolutionDayTranslations.put("de", "Tag der Revolution");
        revolutionDayTranslations.put("it", "Giorno della Rivoluzione");
        revolutionDayTranslations.put("ko", "혁명의 날");
        revolutionDayTranslations.put("zh", "革命节");
        revolutionDayTranslations.put("zh-TW", "革命節");
        revolutionDayTranslations.put("ja", "革命の日");
        revolutionDayTranslations.put("ar", "يوم الثورة");
        manualTranslations.put("Revolution Day", revolutionDayTranslations);

        // Epiphany
        Map<String, String> epiphanyTranslations = new HashMap<>();
        epiphanyTranslations.put("es", "Epifanía");
        epiphanyTranslations.put("fr", "Épiphanie");
        epiphanyTranslations.put("de", "Erscheinung");
        epiphanyTranslations.put("it", "Epifania");
        epiphanyTranslations.put("ko", "주현절");
        epiphanyTranslations.put("zh", "主显节");
        epiphanyTranslations.put("zh-TW", "主顯節");
        epiphanyTranslations.put("ja", "聖誕節");
        epiphanyTranslations.put("ar", "عيد الظهور");
        manualTranslations.put("Epiphany", epiphanyTranslations);

        // Hispanic Day
        Map<String, String> hispanicDayTranslations = new HashMap<>();
        hispanicDayTranslations.put("es", "Día de la Hispanidad");
        hispanicDayTranslations.put("fr", "Jour de l'Hispanité");
        hispanicDayTranslations.put("de", "Tag der Hispanität");
        hispanicDayTranslations.put("it", "Giorno della Hispanità");
        hispanicDayTranslations.put("ko", "히스패니즘의 날");
        hispanicDayTranslations.put("zh", "西班牙日");
        hispanicDayTranslations.put("zh-TW", "西班牙日");
        hispanicDayTranslations.put("ja", "スペイン語の日");
        hispanicDayTranslations.put("ar", "يوم الثقافة الإسبانية");
        manualTranslations.put("Hispanic Day", hispanicDayTranslations);

        // Easter Sunday
        Map<String, String> easterSundayTranslations = new HashMap<>();
        easterSundayTranslations.put("es", "Domingo de Pascua");
        easterSundayTranslations.put("fr", "Dimanche de Pâques");
        easterSundayTranslations.put("de", "Ostersonntag");
        easterSundayTranslations.put("it", "Domenica di Pasqua");
        easterSundayTranslations.put("ko", "부활절");
        easterSundayTranslations.put("zh", "复活节 Sunday");
        easterSundayTranslations.put("zh-TW", "復活節星期日");
        easterSundayTranslations.put("ja", "復活祭日");
        easterSundayTranslations.put("ar", "أحد القيامة");
        manualTranslations.put("Easter Sunday", easterSundayTranslations);

        // White Sunday
        Map<String, String> whiteSundayTranslations = new HashMap<>();
        whiteSundayTranslations.put("es", "Domingo de la Candelaria");
        whiteSundayTranslations.put("fr", "Dimanche de la Chandeleur");
        whiteSundayTranslations.put("de", "Witwen-Sonntag");
        whiteSundayTranslations.put("it", "Domenica della Candelaria");
        whiteSundayTranslations.put("ko", "촛불절");
        whiteSundayTranslations.put("zh", "圣烛节");
        whiteSundayTranslations.put("zh-TW", "聖燭節");
        whiteSundayTranslations.put("ja", "燭台の日");
        whiteSundayTranslations.put("ar", "الأحد الأبيض");
        manualTranslations.put("White Sunday", whiteSundayTranslations);

        // Corpus Christi
        Map<String, String> corpusChristiTranslations = new HashMap<>();
        corpusChristiTranslations.put("es", "Corpus Christi");
        corpusChristiTranslations.put("fr", "Corps de Christ");
        corpusChristiTranslations.put("de", "Corpus Christi");
        corpusChristiTranslations.put("it", "Corpus Domini");
        corpusChristiTranslations.put("ko", "성체절");
        corpusChristiTranslations.put("zh", "圣体节");
        corpusChristiTranslations.put("zh-TW", "聖體節");
        corpusChristiTranslations.put("ja", "聖体祭");
        corpusChristiTranslations.put("ar", "عيد جسد المسيح");
        manualTranslations.put("Corpus Christi", corpusChristiTranslations);

        //Valentine's Day
        Map<String, String> valentinesDayTranslations = new HashMap<>();
        valentinesDayTranslations.put("es", "Día de San Valentín");
        valentinesDayTranslations.put("fr", "Saint-Valentin");
        valentinesDayTranslations.put("de", "Valentinstag");
        valentinesDayTranslations.put("it", "San Valentino");
        valentinesDayTranslations.put("ko", "발렌타인 데이");
        valentinesDayTranslations.put("zh", "情人节");
        valentinesDayTranslations.put("zh-TW", "情人節");
        valentinesDayTranslations.put("ja", "バレンタインデー");
        valentinesDayTranslations.put("ar", "عيد الحب");
        manualTranslations.put("Valentine's Day", valentinesDayTranslations);

        // Halloween
        Map<String, String> halloweenTranslations = new HashMap<>();
        halloweenTranslations.put("es", "Halloween");
        halloweenTranslations.put("fr", "Halloween");
        halloweenTranslations.put("de", "Halloween");
        halloweenTranslations.put("it", "Halloween");
        halloweenTranslations.put("ko", "할로윈");
        halloweenTranslations.put("zh", "万圣节");
        halloweenTranslations.put("zh-TW", "萬聖節");
        halloweenTranslations.put("ja", "ハロウィン");
        halloweenTranslations.put("ar", "الهالووين");
        manualTranslations.put("Halloween", halloweenTranslations);

        // German Unity Day
        Map<String, String> germanUnityDayTranslations = new HashMap<>();
        germanUnityDayTranslations.put("es", "Día de la Unidad Alemana");
        germanUnityDayTranslations.put("fr", "Jour de l'Unité allemande");
        germanUnityDayTranslations.put("de", "Tag der Deutschen Einheit");
        germanUnityDayTranslations.put("it", "Giorno dell'Unità tedesca");
        germanUnityDayTranslations.put("ko", "독일 통일의 날");
        germanUnityDayTranslations.put("zh", "德国统一日");
        germanUnityDayTranslations.put("zh-TW", "德國統一日");
        germanUnityDayTranslations.put("ja", "ドイツ統一の日");
        germanUnityDayTranslations.put("ar", "يوم الوحدة الألمانية");
        manualTranslations.put("German Unity Day", germanUnityDayTranslations);


        // General terms
        Map<String, String> monthTranslations = new HashMap<>();
        monthTranslations.put("es", "Mes:");
        monthTranslations.put("fr", "Mois:");
        monthTranslations.put("de", "Monat:");
        monthTranslations.put("it", "Mese:");
        monthTranslations.put("ko", "\uC6D4:");
        monthTranslations.put("pl", "Miesiąc:");
        monthTranslations.put("zh", "\u6708:");
        monthTranslations.put("zh-TW", "\u6708:");
        monthTranslations.put("ja", "\u6708:");
        monthTranslations.put("ar", "الشهر:");
        manualTranslations.put("Month:", monthTranslations);

        Map<String, String> yearTranslations = new HashMap<>();
        yearTranslations.put("es", "A\u00F1o:");
        yearTranslations.put("fr", "Ann\u00E9e:");
        yearTranslations.put("de", "Jahr:");
        yearTranslations.put("it", "Anno:");
        yearTranslations.put("ko", "\uB144:");
        yearTranslations.put("pl", "Rok:");
        yearTranslations.put("zh", "\u5E74:");
        yearTranslations.put("zh-TW", "\u5E74:");
        yearTranslations.put("ja", "\u5E74:");
        yearTranslations.put("ar", "السنة:");
        manualTranslations.put("Year:", yearTranslations);

        Map<String, String> languageTranslations = new HashMap<>();
        languageTranslations.put("es", "Idioma:");
        languageTranslations.put("fr", "Langue:");
        languageTranslations.put("de", "Sprache:");
        languageTranslations.put("it", "Lingua:");
        languageTranslations.put("ko", "\uC5B8\uC5B4:");
        languageTranslations.put("pl", "Język:");
        languageTranslations.put("zh", "\u8BED\u8A00:");
        languageTranslations.put("zh-TW", "\u8A9E\u8A00:");
        languageTranslations.put("ja", "\u8A9E\u8A00:");
        languageTranslations.put("ar", "اللغة:");
        manualTranslations.put("Language:", languageTranslations);

        Map<String, String> todayTranslations = new HashMap<>();
        todayTranslations.put("es", "Hoy:");
        todayTranslations.put("fr", "Aujourd'hui:");
        todayTranslations.put("de", "Heute:");
        todayTranslations.put("it", "Oggi:");
        todayTranslations.put("ko", "\uC624\uB298:");
        todayTranslations.put("pl", "Dzisiaj:");
        todayTranslations.put("zh", "\u4ECA\u5929:");
        todayTranslations.put("zh-TW", "\u4ECA\u5929:");
        todayTranslations.put("ja", "\u4ECA\u65E5:");
        todayTranslations.put("ar", "اليوم:");
        manualTranslations.put("Today:", todayTranslations);

        Map<String, String> upcomingEventsTranslations = new HashMap<>();
        upcomingEventsTranslations.put("es", "Próximos eventos");
        upcomingEventsTranslations.put("fr", "Événements à venir");
        upcomingEventsTranslations.put("de", "Bevorstehende Ereignisse");
        upcomingEventsTranslations.put("it", "Eventi in arrivo");
        upcomingEventsTranslations.put("ko", "다가오는 이벤트");
        upcomingEventsTranslations.put("pl", "Nadchodzące wydarzenia");
        upcomingEventsTranslations.put("zh", "即将发生的事件");
        upcomingEventsTranslations.put("zh-TW", "即將發生的事件");
        upcomingEventsTranslations.put("ja", "今後のイベント");
        upcomingEventsTranslations.put("ar", "الأحداث القادمة");
        manualTranslations.put("Upcoming events", upcomingEventsTranslations);

        Map<String, String> noUpcomingEventsTranslations = new HashMap<>();
        noUpcomingEventsTranslations.put("es", "No hay eventos próximos.");
        noUpcomingEventsTranslations.put("fr", "Aucun événement à venir.");
        noUpcomingEventsTranslations.put("de", "Keine bevorstehenden Ereignisse.");
        noUpcomingEventsTranslations.put("it", "Nessun evento in programma.");
        noUpcomingEventsTranslations.put("ko", "예정된 이벤트가 없습니다.");
        noUpcomingEventsTranslations.put("pl", "Brak nadchodzących wydarzeń.");
        noUpcomingEventsTranslations.put("zh", "没有即将发生的事件。");
        noUpcomingEventsTranslations.put("zh-TW", "沒有即將發生的事件。");
        noUpcomingEventsTranslations.put("ja", "今後のイベントはありません。");
        noUpcomingEventsTranslations.put("ar", "لا توجد أحداث قادمة.");
        manualTranslations.put("No upcoming events.", noUpcomingEventsTranslations);

        Map<String, String> dateTranslations = new HashMap<>();
        dateTranslations.put("es", "Fecha:");
        dateTranslations.put("fr", "Date:");
        dateTranslations.put("de", "Datum:");
        dateTranslations.put("it", "Data:");
        dateTranslations.put("ko", "날짜:");
        dateTranslations.put("zh", "日期:");
        dateTranslations.put("zh-TW", "日期:");
        dateTranslations.put("ja", "日付:");
        dateTranslations.put("ar", "التاريخ:");
        manualTranslations.put("Date:", dateTranslations);

        Map<String, String> timeTranslations = new HashMap<>();
        timeTranslations.put("es", "Hora:");
        timeTranslations.put("fr", "Heure:");
        timeTranslations.put("de", "Uhrzeit:");
        timeTranslations.put("it", "Ora:");
        timeTranslations.put("ko", "시간:");
        timeTranslations.put("zh", "时间:");
        timeTranslations.put("zh-TW", "時間:");
        timeTranslations.put("ja", "時間:");
        timeTranslations.put("ar", "الوقت:");
        manualTranslations.put("Time:", timeTranslations);

        Map<String, String> startTranslations = new HashMap<>();
        startTranslations.put("es", "Inicio:");
        startTranslations.put("fr", "Début:");
        startTranslations.put("de", "Start:");
        startTranslations.put("it", "Inizio:");
        startTranslations.put("ko", "시작:");
        startTranslations.put("zh", "开始:");
        startTranslations.put("zh-TW", "開始:");
        startTranslations.put("ja", "開始:");
        startTranslations.put("ar", "بدء:");
        manualTranslations.put("Start:", startTranslations);

        Map<String, String> endTranslations = new HashMap<>();
        endTranslations.put("es", "Fin:");
        endTranslations.put("fr", "Fin:");
        endTranslations.put("de", "Ende:");
        endTranslations.put("it", "Fine:");
        endTranslations.put("ko", "종료:");
        endTranslations.put("zh", "结束:");
        endTranslations.put("zh-TW", "結束:");
        endTranslations.put("ja", "終了:");
        endTranslations.put("ar", "انتهاء:");
        manualTranslations.put("End:", endTranslations);

        Map<String, String> locationTranslations = new HashMap<>();
        locationTranslations.put("es", "Ubicación:");
        locationTranslations.put("fr", "Lieu:");
        locationTranslations.put("de", "Ort:");
        locationTranslations.put("it", "Luogo:");
        locationTranslations.put("ko", "장소:");
        locationTranslations.put("zh", "地点:");
        locationTranslations.put("zh-TW", "地點:");
        locationTranslations.put("ja", "場所:");
        locationTranslations.put("ar", "الموقع:");
        manualTranslations.put("Location:", locationTranslations);

        Map<String, String> notesTranslations = new HashMap<>();
        notesTranslations.put("es", "Notas:");
        notesTranslations.put("fr", "Notes:");
        notesTranslations.put("de", "Notizen:");
        notesTranslations.put("it", "Note:");
        notesTranslations.put("ko", "메모:");
        notesTranslations.put("zh", "备注:");
        notesTranslations.put("zh-TW", "備註:");
        notesTranslations.put("ja", "メモ:");
        notesTranslations.put("ar", "ملاحظات:");
        manualTranslations.put("Notes:", notesTranslations);

        Map<String, String> eventsForTranslations = new HashMap<>();
        eventsForTranslations.put("es", "Eventos para");
        eventsForTranslations.put("fr", "Événements pour");
        eventsForTranslations.put("de", "Ereignisse für");
        eventsForTranslations.put("it", "Eventi per");
        eventsForTranslations.put("ko", "이벤트");
        eventsForTranslations.put("zh", "事件于");
        eventsForTranslations.put("zh-TW", "事件於");
        eventsForTranslations.put("ja", "イベント：");
        eventsForTranslations.put("ar", "أحداث لـ");
        manualTranslations.put("Events for", eventsForTranslations);

        Map<String, String> eventsOnTranslations = new HashMap<>();
        eventsOnTranslations.put("es", "Eventos el");
        eventsOnTranslations.put("fr", "Événements le");
        eventsOnTranslations.put("de", "Ereignisse am");
        eventsOnTranslations.put("it", "Eventi il");
        eventsOnTranslations.put("ko", "이벤트 날짜");
        eventsOnTranslations.put("zh", "事件于");
        eventsOnTranslations.put("zh-TW", "事件於");
        eventsOnTranslations.put("ja", "イベントの日：");
        eventsOnTranslations.put("ar", "أحداث في");
        manualTranslations.put("Events on", eventsOnTranslations);

        Map<String, String> eventsTranslations = new HashMap<>();
        eventsTranslations.put("es", "Eventos");
        eventsTranslations.put("fr", "Événements");
        eventsTranslations.put("de", "Ereignisse");
        eventsTranslations.put("it", "Eventi");
        eventsTranslations.put("ko", "이벤트");
        eventsTranslations.put("zh", "事件");
        eventsTranslations.put("zh-TW", "事件");
        eventsTranslations.put("ja", "イベント");
        eventsTranslations.put("ar", "الأحداث");
        manualTranslations.put("Events", eventsTranslations);

        Map<String, String> addEventTranslations = new HashMap<>();
        addEventTranslations.put("es", "Agregar evento");
        addEventTranslations.put("fr", "Ajouter un événement");
        addEventTranslations.put("de", "Ereignis hinzufügen");
        addEventTranslations.put("it", "Aggiungi evento");
        addEventTranslations.put("ko", "이벤트 추가");
        addEventTranslations.put("zh", "添加事件");
        addEventTranslations.put("zh-TW", "新增事件");
        addEventTranslations.put("ja", "イベントを追加");
        addEventTranslations.put("ar", "إضافة حدث");
        manualTranslations.put("Add Event", addEventTranslations);

        Map<String, String> deleteSelectedTranslations = new HashMap<>();
        deleteSelectedTranslations.put("es", "Eliminar seleccionado");
        deleteSelectedTranslations.put("fr", "Supprimer la sélection");
        deleteSelectedTranslations.put("de", "Auswahl löschen");
        deleteSelectedTranslations.put("it", "Elimina selezionato");
        deleteSelectedTranslations.put("ko", "선택 삭제");
        deleteSelectedTranslations.put("zh", "删除所选");
        deleteSelectedTranslations.put("zh-TW", "刪除所選");
        deleteSelectedTranslations.put("ja", "選択を削除");
        deleteSelectedTranslations.put("ar", "حذف المحدد");
        manualTranslations.put("Delete Selected", deleteSelectedTranslations);

        Map<String, String> deleteSelectedEventTranslations = new HashMap<>();
        deleteSelectedEventTranslations.put("es", "¿Eliminar el evento seleccionado?");
        deleteSelectedEventTranslations.put("fr", "Supprimer l'événement sélectionné ?");
        deleteSelectedEventTranslations.put("de", "Ausgewähltes Ereignis löschen?");
        deleteSelectedEventTranslations.put("it", "Eliminare l'evento selezionato?");
        deleteSelectedEventTranslations.put("ko", "선택한 이벤트를 삭제하시겠습니까?");
        deleteSelectedEventTranslations.put("zh", "删除所选事件？");
        deleteSelectedEventTranslations.put("zh-TW", "刪除所選事件？");
        deleteSelectedEventTranslations.put("ja", "選択したイベントを削除しますか？");
        deleteSelectedEventTranslations.put("ar", "هل تريد حذف الحدث المحدد؟");
        manualTranslations.put("Delete selected event?", deleteSelectedEventTranslations);

        Map<String, String> confirmDeleteTranslations = new HashMap<>();
        confirmDeleteTranslations.put("es", "Confirmar eliminación");
        confirmDeleteTranslations.put("fr", "Confirmer la suppression");
        confirmDeleteTranslations.put("de", "Löschen bestätigen");
        confirmDeleteTranslations.put("it", "Conferma eliminazione");
        confirmDeleteTranslations.put("ko", "삭제 확인");
        confirmDeleteTranslations.put("zh", "确认删除");
        confirmDeleteTranslations.put("zh-TW", "確認刪除");
        confirmDeleteTranslations.put("ja", "削除を確認");
        confirmDeleteTranslations.put("ar", "تأكيد الحذف");
        manualTranslations.put("Confirm Delete", confirmDeleteTranslations);

        Map<String, String> pleaseEnterEventNameTranslations = new HashMap<>();
        pleaseEnterEventNameTranslations.put("es", "Por favor ingrese un nombre de evento.");
        pleaseEnterEventNameTranslations.put("fr", "Veuillez entrer un nom d'événement.");
        pleaseEnterEventNameTranslations.put("de", "Bitte geben Sie einen Ereignisnamen ein.");
        pleaseEnterEventNameTranslations.put("it", "Inserisci un nome per l'evento.");
        pleaseEnterEventNameTranslations.put("ko", "이벤트 이름을 입력하세요.");
        pleaseEnterEventNameTranslations.put("zh", "请输入事件名称。");
        pleaseEnterEventNameTranslations.put("zh-TW", "請輸入事件名稱。");
        pleaseEnterEventNameTranslations.put("ja", "イベント名を入力してください。");
        pleaseEnterEventNameTranslations.put("ar", "يرجى إدخال اسم الحدث.");
        manualTranslations.put("Please enter an event name.", pleaseEnterEventNameTranslations);

        Map<String, String> missingNameTranslations = new HashMap<>();
        missingNameTranslations.put("es", "Nombre faltante");
        missingNameTranslations.put("fr", "Nom manquant");
        missingNameTranslations.put("de", "Name fehlt");
        missingNameTranslations.put("it", "Nome mancante");
        missingNameTranslations.put("ko", "이름 누락");
        missingNameTranslations.put("zh", "名称缺失");
        missingNameTranslations.put("zh-TW", "名稱缺失");
        missingNameTranslations.put("ja", "名前が不足しています");
        missingNameTranslations.put("ar", "الاسم مفقود");
        manualTranslations.put("Missing name", missingNameTranslations);

        Map<String, String> invalidTimeTranslations = new HashMap<>();
        invalidTimeTranslations.put("es", "Hora inválida");
        invalidTimeTranslations.put("fr", "Heure invalide");
        invalidTimeTranslations.put("de", "Ungültige Uhrzeit");
        invalidTimeTranslations.put("it", "Ora non valida");
        invalidTimeTranslations.put("ko", "잘못된 시간");
        invalidTimeTranslations.put("zh", "无效时间");
        invalidTimeTranslations.put("zh-TW", "無效時間");
        invalidTimeTranslations.put("ja", "無効な時間");
        invalidTimeTranslations.put("ar", "وقت غير صالح");
        manualTranslations.put("Invalid time", invalidTimeTranslations);

        Map<String, String> endBeforeStartTranslations = new HashMap<>();
        endBeforeStartTranslations.put("es", "La fecha/hora de fin debe ser posterior a la fecha/hora de inicio.");
        endBeforeStartTranslations.put("fr", "La date/heure de fin doit être après la date/heure de début.");
        endBeforeStartTranslations.put("de", "Enddatum/-uhrzeit muss nach dem Startdatum/-uhrzeit liegen.");
        endBeforeStartTranslations.put("it", "La data/ora di fine deve essere successiva alla data/ora di inizio.");
        endBeforeStartTranslations.put("ko", "종료 날짜/시간은 시작 날짜/시간 이후여야 합니다.");
        endBeforeStartTranslations.put("zh", "结束日期/时间必须在开始日期/时间之后。");
        endBeforeStartTranslations.put("zh-TW", "結束日期/時間必須在開始日期/時間之後。");
        endBeforeStartTranslations.put("ja", "終了日時は開始日時の後でなければなりません。");
        endBeforeStartTranslations.put("ar", "يجب أن تكون تاريخ/وقت الانتهاء بعد تاريخ/وقت البدء.");
        manualTranslations.put("End date/time must be after start date/time.", endBeforeStartTranslations);

        Map<String, String> addEventForTranslations = new HashMap<>();
        addEventForTranslations.put("es", "Agregar evento para");
        addEventForTranslations.put("fr", "Ajouter un événement pour");
        addEventForTranslations.put("de", "Ereignis hinzufügen für");
        addEventForTranslations.put("it", "Aggiungi evento per");
        addEventForTranslations.put("ko", "다음 날짜에 이벤트 추가:");
        addEventForTranslations.put("zh", "添加事件于");
        addEventForTranslations.put("zh-TW", "新增事件於");
        addEventForTranslations.put("ja", "次のイベントを追加：");
        addEventForTranslations.put("ar", "إضافة حدث لـ");
        manualTranslations.put("Add event for", addEventForTranslations);

        Map<String, String> eventNameTranslations = new HashMap<>();
        eventNameTranslations.put("es", "Nombre del evento:");
        eventNameTranslations.put("fr", "Nom de l'événement:");
        eventNameTranslations.put("de", "Ereignisname:");
        eventNameTranslations.put("it", "Nome dell'evento:");
        eventNameTranslations.put("ko", "이벤트 이름:");
        eventNameTranslations.put("zh", "事件名称:");
        eventNameTranslations.put("zh-TW", "事件名稱:");
        eventNameTranslations.put("ja", "イベント名:");
        eventNameTranslations.put("ar", "اسم الحدث:");
        manualTranslations.put("Event name:", eventNameTranslations);

        Map<String, String> startDateTranslations = new HashMap<>();
        startDateTranslations.put("es", "Fecha de inicio:");
        startDateTranslations.put("fr", "Date de début:");
        startDateTranslations.put("de", "Startdatum:");
        startDateTranslations.put("it", "Data di inizio:");
        startDateTranslations.put("ko", "시작 날짜:");
        startDateTranslations.put("zh", "开始日期:");
        startDateTranslations.put("zh-TW", "開始日期:");
        startDateTranslations.put("ja", "開始日:");
        startDateTranslations.put("ar", "تاريخ البدء:");
        manualTranslations.put("Start date:", startDateTranslations);

        Map<String, String> startTimeTranslations = new HashMap<>();
        startTimeTranslations.put("es", "Hora de inicio:");
        startTimeTranslations.put("fr", "Heure de début:");
        startTimeTranslations.put("de", "Startzeit:");
        startTimeTranslations.put("it", "Ora di inizio:");
        startTimeTranslations.put("ko", "시작 시간:");
        startTimeTranslations.put("zh", "开始时间:");
        startTimeTranslations.put("zh-TW", "開始時間:");
        startTimeTranslations.put("ja", "開始時間:");
        startTimeTranslations.put("ar", "وقت البدء:");
        manualTranslations.put("Start time:", startTimeTranslations);

        Map<String, String> endDateTranslations = new HashMap<>();
        endDateTranslations.put("es", "Fecha de fin:");
        endDateTranslations.put("fr", "Date de fin:");
        endDateTranslations.put("de", "Enddatum:");
        endDateTranslations.put("it", "Data di fine:");
        endDateTranslations.put("ko", "종료 날짜:");
        endDateTranslations.put("zh", "结束日期:");
        endDateTranslations.put("zh-TW", "結束日期:");
        endDateTranslations.put("ja", "終了日:");
        endDateTranslations.put("ar", "تاريخ الانتهاء:");
        manualTranslations.put("End date:", endDateTranslations);

        Map<String, String> endTimeTranslations = new HashMap<>();
        endTimeTranslations.put("es", "Hora de fin:");
        endTimeTranslations.put("fr", "Heure de fin:");
        endTimeTranslations.put("de", "Endzeit:");
        endTimeTranslations.put("it", "Ora di fine:");
        endTimeTranslations.put("ko", "종료 시간:");
        endTimeTranslations.put("zh", "结束时间:");
        endTimeTranslations.put("zh-TW", "結束時間:");
        endTimeTranslations.put("ja", "終了時間:");
        endTimeTranslations.put("ar", "وقت الانتهاء:");
        manualTranslations.put("End time:", endTimeTranslations);

        Map<String, String> closeTranslations = new HashMap<>();
        closeTranslations.put("es", "Cerrar");
        closeTranslations.put("fr", "Fermer");
        closeTranslations.put("de", "Schließen");
        closeTranslations.put("it", "Chiudi");
        closeTranslations.put("ko", "닫기");
        closeTranslations.put("zh", "关闭");
        closeTranslations.put("zh-TW", "關閉");
        closeTranslations.put("ja", "閉じる");
        closeTranslations.put("ar", "إغلاق");
        manualTranslations.put("Close", closeTranslations);

        Map<String, String> editSelectedTranslations = new HashMap<>();
        editSelectedTranslations.put("es", "Editar seleccionado");
        editSelectedTranslations.put("fr", "Modifier la sélection");
        editSelectedTranslations.put("de", "Auswahl bearbeiten");
        editSelectedTranslations.put("it", "Modifica selezionato");
        editSelectedTranslations.put("ko", "선택 항목 편집");
        editSelectedTranslations.put("zh", "编辑所选");
        editSelectedTranslations.put("zh-TW", "編輯所選");
        editSelectedTranslations.put("ja", "選択を編集");
        editSelectedTranslations.put("ar", "تحرير المحدد");
        manualTranslations.put("Edit Selected", editSelectedTranslations);

        Map<String, String> editEventTranslations = new HashMap<>();
        editEventTranslations.put("es", "Editar evento");
        editEventTranslations.put("fr", "Modifier l'événement");
        editEventTranslations.put("de", "Ereignis bearbeiten");
        editEventTranslations.put("it", "Modifica evento");
        editEventTranslations.put("ko", "이벤트 편집");
        editEventTranslations.put("zh", "编辑事件");
        editEventTranslations.put("zh-TW", "編輯事件");
        editEventTranslations.put("ja", "イベントを編集");
        editEventTranslations.put("ar", "تحرير الحدث");
        manualTranslations.put("Edit event", editEventTranslations);

        Map<String, String> saveTranslations = new HashMap<>();
        saveTranslations.put("es", "Guardar");
        saveTranslations.put("fr", "Enregistrer");
        saveTranslations.put("de", "Speichern");
        saveTranslations.put("it", "Salva");
        saveTranslations.put("ko", "저장");
        saveTranslations.put("zh", "保存");
        saveTranslations.put("zh-TW", "保存");
        saveTranslations.put("ja", "保存");
        saveTranslations.put("ar", "حفظ");
        manualTranslations.put("Save", saveTranslations);

        Map<String, String> cancelTranslations = new HashMap<>();
        cancelTranslations.put("es", "Cancelar");
        cancelTranslations.put("fr", "Annuler");
        cancelTranslations.put("de", "Abbrechen");
        cancelTranslations.put("it", "Annulla");
        cancelTranslations.put("ko", "취소");
        cancelTranslations.put("zh", "取消");
        cancelTranslations.put("zh-TW", "取消");
        cancelTranslations.put("ja", "キャンセル");
        cancelTranslations.put("ar", "إلغاء");
        manualTranslations.put("Cancel", cancelTranslations);

        Map<String, String> yesTranslations = new HashMap<>();
        yesTranslations.put("es", "Sí");
        yesTranslations.put("fr", "Oui");
        yesTranslations.put("de", "Ja");
        yesTranslations.put("it", "Sì");
        yesTranslations.put("ko", "예");
        yesTranslations.put("zh", "是");
        yesTranslations.put("zh-TW", "是");
        yesTranslations.put("ja", "はい");
        yesTranslations.put("ar", "نعم");
        manualTranslations.put("Yes", yesTranslations);

        Map<String, String> noTranslations = new HashMap<>();
        noTranslations.put("es", "No");
        noTranslations.put("fr", "Non");
        noTranslations.put("de", "Nein");
        noTranslations.put("it", "No");
        noTranslations.put("ko", "아니요");
        noTranslations.put("zh", "否");
        noTranslations.put("zh-TW", "否");
        noTranslations.put("ja", "いいえ");
        noTranslations.put("ar", "لا");
        manualTranslations.put("No", noTranslations);

        Map<String, String> okTranslations = new HashMap<>();
        okTranslations.put("es", "OK");
        okTranslations.put("fr", "OK");
        okTranslations.put("de", "OK");
        okTranslations.put("it", "OK");
        okTranslations.put("ko", "확인");
        okTranslations.put("zh", "确定");
        okTranslations.put("zh-TW", "確定");
        okTranslations.put("ja", "OK");
        okTranslations.put("ar", "حسناً");
        manualTranslations.put("OK", okTranslations);

        Map<String, String> holidayTranslations = new HashMap<>();
        holidayTranslations.put("es", "Vacaciones");
        holidayTranslations.put("fr", "Vacances");
        holidayTranslations.put("de", "Feiertag");
        holidayTranslations.put("it", "Vacanza");
        holidayTranslations.put("ko", "휴일");
        holidayTranslations.put("zh", "假日");
        holidayTranslations.put("zh-TW", "假日");
        holidayTranslations.put("ja", "休日");
        holidayTranslations.put("ar", "عطلة");
        manualTranslations.put("Holiday", holidayTranslations);

        Map<String, String> appTitleTranslations = new HashMap<>();
        appTitleTranslations.put("es", "Aplicación de calendario");
        appTitleTranslations.put("fr", "Application de calendrier");
        appTitleTranslations.put("de", "Kalenderanwendung");
        appTitleTranslations.put("it", "Applicazione del calendario");
        appTitleTranslations.put("ko", "달력 애플리케이션");
        appTitleTranslations.put("zh", "日历应用程序");
        appTitleTranslations.put("zh-TW", "日曆應用程式");
        appTitleTranslations.put("ja", "カレンダーアプリ");
        appTitleTranslations.put("ar", "تطبيق التقويم");
        manualTranslations.put("Calendar Application", appTitleTranslations);
    }

    private void initializePolishTranslations() {
        Map<String, String> polishTranslations = new HashMap<>();
        polishTranslations.put("Month:", "Miesiąc:");
        polishTranslations.put("Year:", "Rok:");
        polishTranslations.put("Language:", "Język:");
        polishTranslations.put("Today:", "Dzisiaj:");
        polishTranslations.put("Country:", "Kraj:");
        polishTranslations.put("Upcoming events", "Nadchodzące wydarzenia");
        polishTranslations.put("No upcoming events.", "Brak nadchodzących wydarzeń.");
        polishTranslations.put("Add Entry", "Dodaj wpis");
        polishTranslations.put("Delete Entry", "Usuń wpis");
        polishTranslations.put("Edit Entry", "Edytuj wpis");
        polishTranslations.put("Bullet Journal", "Bullet Journal");
        polishTranslations.put("Settings", "Ustawienia");
        polishTranslations.put("Date:", "Data:");
        polishTranslations.put("Time:", "Czas:");
        polishTranslations.put("Start:", "Początek:");
        polishTranslations.put("End:", "Koniec:");
        polishTranslations.put("Location:", "Lokalizacja:");
        polishTranslations.put("Notes:", "Notatki:");
        polishTranslations.put("Events for", "Wydarzenia dla");
        polishTranslations.put("Events on", "Wydarzenia w dniu");
        polishTranslations.put("Events", "Wydarzenia");
        polishTranslations.put("Add Event", "Dodaj wydarzenie");
        polishTranslations.put("Delete Selected", "Usuń zaznaczone");
        polishTranslations.put("Delete selected event?", "Usunąć zaznaczone wydarzenie?");
        polishTranslations.put("Confirm Delete", "Potwierdź usunięcie");
        polishTranslations.put("Please enter an event name.", "Proszę podać nazwę wydarzenia.");
        polishTranslations.put("Missing name", "Brakuje nazwy");
        polishTranslations.put("End date/time must be after start date/time.", "Data/czas zakończenia musi być późniejsza niż data/czas rozpoczęcia.");
        polishTranslations.put("Invalid time", "Nieprawidłowy czas");
        polishTranslations.put("Add event for", "Dodaj wydarzenie dla");
        polishTranslations.put("Event name:", "Nazwa wydarzenia:");
        polishTranslations.put("Start date:", "Data rozpoczęcia:");
        polishTranslations.put("Start time:", "Godzina rozpoczęcia:");
        polishTranslations.put("End date:", "Data zakończenia:");
        polishTranslations.put("End time:", "Godzina zakończenia:");
        polishTranslations.put("Close", "Zamknij");
        polishTranslations.put("Save", "Zapisz");
        polishTranslations.put("Cancel", "Anuluj");
        polishTranslations.put("Edit Selected", "Edytuj zaznaczone");
        polishTranslations.put("Edit event", "Edytuj wydarzenie");
        polishTranslations.put("Holiday", "Święto");
        polishTranslations.put("Remembrance Day", "Dzień Pamięci");
        polishTranslations.put("Seollal", "Seollal");
        polishTranslations.put("Seollal Eve", "Wigilia Seollal");
        polishTranslations.put("Seollal Holiday", "Święto Seollal");
        polishTranslations.put("Additional Seollal Holiday", "Dodatkowe święto Seollal");
        polishTranslations.put("Chuseok Eve", "Wigilia Chuseok");
        polishTranslations.put("Chuseok Holiday", "Święto Chuseok");
        polishTranslations.put("Calendar Application", "Aplikacja kalendarza");
        polishTranslations.put("New Year's Day", "Nowy Rok");
        polishTranslations.put("Family Day", "Dzień Rodziny");
        polishTranslations.put("Good Friday", "Wielki Piątek");
        polishTranslations.put("Victoria Day", "Dzień Wiktorii");
        polishTranslations.put("Canada Day", "Dzień Kanady");
        polishTranslations.put("Civic Holiday", "Święto Obywatelskie");
        polishTranslations.put("Labour Day", "Święto Pracy");
        polishTranslations.put("Thanksgiving", "Święto Dziękczynienia");
        polishTranslations.put("Christmas Day", "Boże Narodzenie");
        polishTranslations.put("Boxing Day", "Drugi dzień świąt");
        polishTranslations.put("MLK Jr. Day", "Dzień MLK Jr.");
        polishTranslations.put("Presidents' Day", "Dzień Prezydentów");
        polishTranslations.put("Memorial Day", "Dzień Pamięci");
        polishTranslations.put("Juneteenth", "Juneteenth");
        polishTranslations.put("Independence Day", "Dzień Niepodległości");
        polishTranslations.put("Labor Day", "Dzień Pracy");
        polishTranslations.put("Christmas", "Boże Narodzenie");
        polishTranslations.put("Independence Movement Day", "Dzień Ruchu Niepodległościowego");
        polishTranslations.put("Parliamentary Election Day", "Dzień wyborów parlamentarnych");
        polishTranslations.put("Children's Day", "Dzień Dziecka");
        polishTranslations.put("Buddha's Birthday", "Urodziny Buddy");
        polishTranslations.put("Liberation Day", "Dzień Wyzwolenia");
        polishTranslations.put("National Foundation Day", "Dzień Założenia Państwa");
        polishTranslations.put("Hangeul Day", "Dzień Hangul");
        polishTranslations.put("Coming of Age Day", "Dzień Dorosłości");
        polishTranslations.put("Foundation Day", "Dzień Założenia");
        polishTranslations.put("Vernal Equinox Day", "Dzień Równonocy Wiosennej");
        polishTranslations.put("Showa Day", "Dzień Showa");
        polishTranslations.put("Constitution Day", "Dzień Konstytucji");
        polishTranslations.put("Greenery Day", "Dzień Zieleni");
        polishTranslations.put("Marine Day", "Dzień Morza");
        polishTranslations.put("Mountain Day", "Dzień Góry");
        polishTranslations.put("Respect for the Aged Day", "Dzień Szacunku dla Starszych");
        polishTranslations.put("Autumnal Equinox Day", "Dzień Równonocy Jesiennej");
        polishTranslations.put("Sports Day", "Dzień Sportu");
        polishTranslations.put("Culture Day", "Dzień Kultury");
        polishTranslations.put("Labor Thanksgiving Day", "Dzień Dziękczynienia Pracy");
        polishTranslations.put("Spring Festival", "Festiwal Wiosny");
        polishTranslations.put("Qingming Festival", "Festiwal Qingming");
        polishTranslations.put("Dragon Boat Festival", "Święto Smoczej Łodzi");
        polishTranslations.put("Mid-Autumn Festival", "Święto Środka Jesieni");
        polishTranslations.put("National Day", "Dzień Narodowy");
        polishTranslations.put("Easter Monday", "Poniedziałek Wielkanocny");
        polishTranslations.put("Victory in Europe Day", "Dzień Zwycięstwa w Europie");
        polishTranslations.put("Ascension Day", "Wniebowstąpienie");
        polishTranslations.put("Whit Monday", "Poniedziałek Zielonoświątkowy");
        polishTranslations.put("Bastille Day", "Dzień Bastylii");
        polishTranslations.put("Assumption of Mary", "Wniebowzięcie Maryi");
        polishTranslations.put("All Saints' Day", "Wszystkich Świętych");
        polishTranslations.put("Armistice Day", "Dzień Rozejmu");
        polishTranslations.put("Benito Juárez's Birthday", "Urodziny Benito Juáreza");
        polishTranslations.put("Maundy Thursday", "Wielki Czwartek");
        polishTranslations.put("Revolution Day", "Dzień Rewolucji");
        polishTranslations.put("Epiphany", "Objawienie Pańskie");
        polishTranslations.put("Hispanic Day", "Dzień Latynoski");
        polishTranslations.put("Easter Sunday", "Wielkanoc");
        polishTranslations.put("White Sunday", "Biała Niedziela");
        polishTranslations.put("Corpus Christi", "Boże Ciało");
        polishTranslations.put("German Unity Day", "Dzień Jedności Niemiec");
        polishTranslations.put("Valentine's Day", "Walentynki");
        polishTranslations.put("Halloween", "Halloween");
        polishTranslations.put("Delete selected entry?", "Usunąć zaznaczony wpis?");
        polishTranslations.put("Entry text:", "Tekst wpisu:");
        polishTranslations.put("Yes", "Tak");
        polishTranslations.put("No", "Nie");
        polishTranslations.put("OK", "OK");

        for (Map.Entry<String, String> entry : polishTranslations.entrySet()) {
            manualTranslations.computeIfAbsent(entry.getKey(), k -> new HashMap<>()).put("pl", entry.getValue());
        }
    }

    public Locale getLocale(String languageName) {
        return languageMap.getOrDefault(languageName, Locale.ENGLISH);
    }

    public String getLanguageCode(String languageName) {
        return languageCodeMap.getOrDefault(languageName, "en");
    }

    public Set<String> getLanguageNames() {
        return languageMap.keySet();
    }

    public void updateTranslations(String languageName) {
        String targetLang = getLanguageCode(languageName);
        for (String text : translatableTexts) {
            String key = text + "_" + targetLang;
            if (!translations.containsKey(key)) {
                translations.put(key, getTranslated(text, languageName));
            }
        }
    }

    public String getLanguageName(Locale locale) {
        for (Map.Entry<String, Locale> entry : languageMap.entrySet()) {
            if (entry.getValue().equals(locale)) {
                return entry.getKey();
            }
        }
        return "English";
    }

    public String getTranslated(String text, String languageName) {
        String targetLang = getLanguageCode(languageName);
        String key = text + "_" + targetLang;
        if (translations.containsKey(key)) {
            return translations.get(key);
        }
        if (manualTranslations.containsKey(text) && manualTranslations.get(text).containsKey(targetLang)) {
            String translated = manualTranslations.get(text).get(targetLang);
            translations.put(key, translated);
            return translated;
        }
        if (!targetLang.equals("en")) {
            String translated = translateText(text, targetLang);
            translations.put(key, translated);
            return translated;
        }
        return text;
    }

    @SuppressWarnings("CallToPrintStackTrace")
    private String translateText(String text, String targetLang) {
        if (targetLang.equals("en")) {
            return text;
        }
        try {
            String encodedText = URLEncoder.encode(text, "UTF-8");
            String url = "https://libretranslate.com/translate";
            String body = "q=" + encodedText + "&source=en&target=" + targetLang + "&format=text";

            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            con.setDoOutput(true);

            try (OutputStream os = con.getOutputStream()) {
                os.write(body.getBytes(StandardCharsets.UTF_8));
            }

            int responseCode = con.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
                    String inputLine;
                    StringBuilder response = new StringBuilder();
                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    String responseBody = response.toString();
                    int start = responseBody.indexOf("\"translatedText\":\"") + 18;
                    int end = responseBody.indexOf("\"", start);
                    return responseBody.substring(start, end).replace("\\\"", "\"");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return text;
    }
}

// import java.io.InputStream;
// import java.util.HashMap;
// import java.util.Map;
// import java.util.Properties;

// public class Translator {

//     private static Properties properties = new Properties();

//     // Load properties file
//     public static void loadTranslations(String filename) {
//         try (InputStream input = Translator.class
//                 .getClassLoader()
//                 .getResourceAsStream(filename)) {

//             if (input == null) {
//                 System.out.println("File not found: " + filename);
//                 return;
//             }

//             properties.load(input);

//         } catch (Exception e) {
//             e.printStackTrace();
//         }
//     }

//     // Translate with fallback
//     public static String translate(String key, String lang) {
//         String fullKey = key + "." + lang;
//         return properties.getProperty(fullKey, key);
//     }
// }

// // ===================== AUTO-IMPORT SUPPORT =====================

// // No need to pre-register holidays anymore
// // Any missing translation automatically falls back to English

// // ===================== Usage =====================

// // Translator.loadTranslations("translations.properties");

// // Example:
// // String result = Translator.translate("Christmas Day", "ko");
// // → "크리스마스"
