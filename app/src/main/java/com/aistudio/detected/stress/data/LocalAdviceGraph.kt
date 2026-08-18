package com.aistudio.detected.stress.data

enum class StressLevel {
    LOW,
    MODERATE,
    HIGH,
    URGENT
}

data class AdviceItem(
    val id: String,
    val title: String,
    val body: String,
    val categories: Set<String>,
    val minLevel: StressLevel = StressLevel.LOW,
    val maxLevel: StressLevel = StressLevel.HIGH,
    val durationMinutes: Int? = null,
    val requiresProfessionalSupport: Boolean = false,
    val videoUrl: String? = null
)

object LocalAdviceGraph {

    val adviceList = listOf(
        AdviceItem(
            id = "exam_focus_5min",
            title = "تکنیک تمرکز ۵ دقیقه‌ای",
            body = "قبل از شروع درس، ۵ دقیقه فقط روی یک صفحه تمرکز کن.",
            categories = setOf("exam_stress"),
            minLevel = StressLevel.LOW,
            maxLevel = StressLevel.MODERATE,
            durationMinutes = 5
        ),
        AdviceItem(
            id = "exam_meditation",
            title = "مدیتیشن پیش از امتحان",
            body = "۱۰ دقیقه قبل از امتحان، چشمات رو ببند و ۱۰ نفس عمیق بکش.",
            categories = setOf("exam_stress"),
            minLevel = StressLevel.MODERATE,
            maxLevel = StressLevel.HIGH,
            durationMinutes = 10
        ),
        AdviceItem(
            id = "exam_50_10_rule",
            title = "قانون ۵۰/۱۰",
            body = "۵۰ دقیقه درس بخون، ۱۰ دقیقه استراحت کن. مغز نیاز به ریست داره.",
            categories = setOf("exam_stress"),
            minLevel = StressLevel.LOW,
            maxLevel = StressLevel.HIGH
        ),
        AdviceItem(
            id = "exam_write_thoughts",
            title = "نوشتن افکار منفی",
            body = "افکار نگران‌کننده رو روی کاغذ بنویس و دور بریزشون.",
            categories = setOf("exam_stress"),
            minLevel = StressLevel.MODERATE,
            maxLevel = StressLevel.HIGH
        ),
        // === ANXIETY ===
        AdviceItem(
            id = "anxiety_box_breathing",
            title = "تکنیک تنفس ۴-۷-۸",
            body = "این روش به آرامش سیستم عصبی کمک می‌کند. ۴ ثانیه دم، ۷ ثانیه نگه داشتن نفس، و ۸ ثانیه بازدم.",
            categories = setOf("anxiety"),
            minLevel = StressLevel.LOW,
            maxLevel = StressLevel.HIGH,
            durationMinutes = 3,
            videoUrl = "https://www.youtube.com/watch?v=p8fjYPC-k2k"
        ),
        AdviceItem(
            id = "anxiety_grounding_54321",
            title = "تکنیک Grounding (۵-۴-۳-۲-۱)",
            body = "۵ چیز که می‌بینید، ۴ چیز که لمس می‌کنید، ۳ چیز که می‌شنوید، ۲ چیز که می‌بویید و ۱ چیز که می‌چشید را نام ببرید.",
            categories = setOf("anxiety", "anger"),
            minLevel = StressLevel.MODERATE,
            maxLevel = StressLevel.HIGH,
            durationMinutes = 5
        ),
        AdviceItem(
            id = "anxiety_music_432hz",
            title = "موسیقی با فرکانس ۴۳۲ هرتز",
            body = "گوش دادن به موسیقی با این فرکانس به کاهش اضطراب و آرامش ذهن کمک می‌کند.",
            categories = setOf("anxiety", "sleep"),
            minLevel = StressLevel.LOW,
            maxLevel = StressLevel.MODERATE,
            durationMinutes = 15
        ),
        AdviceItem(
            id = "anxiety_body_scan",
            title = "مدیتیشن اسکن بدن",
            body = "یک تمرین ذهن‌آگاهی که در آن تمرکز خود را به آرامی از انگشتان پا تا سر حرکت می‌دهید.",
            categories = setOf("anxiety"),
            minLevel = StressLevel.LOW,
            maxLevel = StressLevel.MODERATE,
            durationMinutes = 10
        ),
        AdviceItem(
            id = "anxiety_book_power_of_now",
            title = "کتاب: نیروی حال (اکهارت تله)",
            body = "آموزش رهایی از نگرانی‌های گذشته و آینده و زندگی در لحظه.",
            categories = setOf("anxiety", "burnout"),
            minLevel = StressLevel.LOW,
            maxLevel = StressLevel.LOW
        ),
        AdviceItem(
            id = "anxiety_journaling",
            title = "نوشتن افکار (ژورنال‌نویسی)",
            body = "نوشتن احساسات روی کاغذ باعث تخلیه بار روانی و نظم ذهنی می‌شود.",
            categories = setOf("anxiety", "depression"),
            minLevel = StressLevel.LOW,
            maxLevel = StressLevel.MODERATE
        ),
        AdviceItem(
            id = "anxiety_tea",
            title = "چای بابونه یا گل‌گاوزبان",
            body = "نوشیدن دمنوش‌های گیاهی آرامش‌بخش می‌تواند به کاهش تپش قلب کمک کند.",
            categories = setOf("anxiety", "sleep"),
            minLevel = StressLevel.LOW,
            maxLevel = StressLevel.MODERATE
        ),
        AdviceItem(
            id = "anxiety_mindful_walk",
            title = "پیاده‌روی آگاهانه",
            body = "بدون گوشی موبایل به مدت ۱۵ دقیقه قدم بزنید و فقط به صدای قدم‌هایتان توجه کنید.",
            categories = setOf("anxiety", "burnout"),
            minLevel = StressLevel.MODERATE,
            maxLevel = StressLevel.HIGH,
            durationMinutes = 15
        ),
        AdviceItem(
            id = "anxiety_belly_breathing",
            title = "تمرکز بر تنفس شکمی",
            body = "دست خود را روی شکم قرار دهید و با تنفس عمیق بالا و پایین رفتن آن را حس کنید.",
            categories = setOf("anxiety"),
            minLevel = StressLevel.LOW,
            maxLevel = StressLevel.HIGH,
            durationMinutes = 3
        ),
        AdviceItem(
            id = "anxiety_mandala",
            title = "کشیدن نقاشی ماندالا",
            body = "رنگ‌آمیزی طرح‌های هندسی و ماندالا باعث تمرکز ذهن و کاهش افکار پراکنده می‌شود.",
            categories = setOf("anxiety"),
            minLevel = StressLevel.LOW,
            maxLevel = StressLevel.MODERATE
        ),
        AdviceItem(
            id = "anxiety_cold_water",
            title = "آب درمانی دست‌ها",
            body = "شستن دست‌ها و مچ با آب خنک برای چند دقیقه، دمای بدن را پایین آورده و حس بهتری می‌دهد.",
            categories = setOf("anxiety", "anger"),
            minLevel = StressLevel.MODERATE,
            maxLevel = StressLevel.HIGH
        ),
        AdviceItem(
            id = "anxiety_thought_stop",
            title = "تکنیک توقف افکار",
            body = "هنگام هجوم افکار منفی، با صدای بلند بگویید «ایست!» و تمرکز خود را به محیط اطراف معطوف کنید.",
            categories = setOf("anxiety", "anger"),
            minLevel = StressLevel.HIGH,
            maxLevel = StressLevel.HIGH
        ),
        // === DEPRESSION (LOW MOOD) ===
        AdviceItem(
            id = "depression_5min_rule",
            title = "قانون ۵ دقیقه",
            body = "اگر کاری سخت به نظر می‌رسد، فقط ۵ دقیقه آن را انجام دهید. معمولاً بعد از ۵ دقیقه ادامه می‌دهید.",
            categories = setOf("depression"),
            minLevel = StressLevel.MODERATE,
            maxLevel = StressLevel.HIGH
        ),
        AdviceItem(
            id = "depression_book_meaning",
            title = "کتاب: انسان در جستجوی معنا",
            body = "خواندن این کتاب به شما کمک می‌کند در سخت‌ترین شرایط زندگی هدف پیدا کنید.",
            categories = setOf("depression"),
            minLevel = StressLevel.LOW,
            maxLevel = StressLevel.LOW
        ),
        AdviceItem(
            id = "depression_sunlight",
            title = "نور خورشید صبحگاهی",
            body = "قرار گرفتن در معرض نور خورشید در ساعات اولیه صبح به تنظیم هورمون سرتونین کمک می‌کند.",
            categories = setOf("depression", "sleep"),
            minLevel = StressLevel.LOW,
            maxLevel = StressLevel.HIGH
        ),
        AdviceItem(
            id = "depression_gratitude",
            title = "شکرگزاری روزانه",
            body = "هر شب ۳ اتفاق مثبت (حتی بسیار کوچک) که در روز افتاده را یادداشت کنید.",
            categories = setOf("depression", "joy"),
            minLevel = StressLevel.LOW,
            maxLevel = StressLevel.MODERATE
        ),
        AdviceItem(
            id = "depression_make_bed",
            title = "مرتب کردن تخت خواب",
            body = "اولین موفقیت روز را با مرتب کردن تخت خوابتان جشن بگیرید.",
            categories = setOf("depression"),
            minLevel = StressLevel.LOW,
            maxLevel = StressLevel.HIGH
        ),
        AdviceItem(
            id = "depression_talk_friend",
            title = "صحبت با یک دوست",
            body = "ارتباط با دیگران یکی از مهمترین راهکارهای مقابله با انزوا است.",
            categories = setOf("depression", "burnout"),
            minLevel = StressLevel.MODERATE,
            maxLevel = StressLevel.HIGH
        ),
        AdviceItem(
            id = "depression_hot_shower",
            title = "دوش آب گرم",
            body = "گرفتن دوش آب گرم باعث شل شدن عضلات و کاهش احساس خستگی مفرط می‌شود.",
            categories = setOf("depression", "sleep"),
            minLevel = StressLevel.LOW,
            maxLevel = StressLevel.HIGH
        ),
        AdviceItem(
            id = "depression_letter_past",
            title = "نوشتن نامه‌ای به خودِ گذشته",
            body = "با خودتان همدلی کنید و بنویسید که چقدر قوی هستید که تا اینجا آمده‌اید.",
            categories = setOf("depression"),
            minLevel = StressLevel.MODERATE,
            maxLevel = StressLevel.HIGH
        ),
        AdviceItem(
            id = "depression_reduce_sugar",
            title = "کاهش مصرف قند مصنوعی",
            body = "قند بالا باعث نوسانات خلقی می‌شود، سعی کنید میوه را جایگزین شیرینی‌جات کنید.",
            categories = setOf("depression", "anxiety"),
            minLevel = StressLevel.LOW,
            maxLevel = StressLevel.MODERATE
        ),
        AdviceItem(
            id = "depression_funny_videos",
            title = "تماشای ویدیوهای خنده‌دار",
            body = "خندیدن حتی به شکل مصنوعی، اندورفین در مغز ترشح می‌کند و روحیه را بالا می‌برد.",
            categories = setOf("depression", "joy"),
            minLevel = StressLevel.LOW,
            maxLevel = StressLevel.MODERATE
        ),
        // === ANGER ===
        AdviceItem(
            id = "anger_timeout",
            title = "تکنیک وقفه (Time-out)",
            body = "هنگام عصبانیت، به مدت ۱۰ دقیقه محیط را ترک کنید تا آدرنالین کاهش یابد.",
            categories = setOf("anger"),
            minLevel = StressLevel.MODERATE,
            maxLevel = StressLevel.HIGH,
            durationMinutes = 10,
            videoUrl = "https://www.youtube.com/watch?v=BsVq5R_F6RA"
        ),
        AdviceItem(
            id = "anger_write_tear",
            title = "نوشتن و پاره کردن",
            body = "عصبانیت خود را روی یک کاغذ بنویسید و سپس آن را پاره کنید و دور بریزید.",
            categories = setOf("anger"),
            minLevel = StressLevel.MODERATE,
            maxLevel = StressLevel.HIGH
        ),
        AdviceItem(
            id = "anger_cold_water_face",
            title = "شستن صورت با آب سرد",
            body = "تماس آب سرد با صورت، سیستم عصبی پاراسمپاتیک را فعال کرده و آرامش می‌آورد.",
            categories = setOf("anger", "anxiety"),
            minLevel = StressLevel.HIGH,
            maxLevel = StressLevel.HIGH
        ),
        AdviceItem(
            id = "anger_cardio",
            title = "ورزش‌های هوازی تند",
            body = "دویدن یا طناب زدن انرژی منفی انباشته شده را تخلیه می‌کند.",
            categories = setOf("anger"),
            minLevel = StressLevel.MODERATE,
            maxLevel = StressLevel.HIGH
        ),
        AdviceItem(
            id = "anger_paper_bag",
            title = "تنفس عمیق کاغذی",
            body = "نفس خود را داخل یک پاکت کاغذی بکشید تا سطح کربن دی اکسید تنظیم شود.",
            categories = setOf("anger", "anxiety"),
            minLevel = StressLevel.HIGH,
            maxLevel = StressLevel.HIGH
        ),
        AdviceItem(
            id = "anger_countdown",
            title = "شمارش معکوس از ۱۰۰",
            body = "شمارش معکوس از ۱۰۰ با فواصل ۳ تایی (۱۰۰، ۹۷، ۹۴...) بخش منطقی مغز را فعال می‌کند.",
            categories = setOf("anger"),
            minLevel = StressLevel.MODERATE,
            maxLevel = StressLevel.HIGH
        ),
        AdviceItem(
            id = "anger_observe",
            title = "مشاهده از بیرون",
            body = "تصور کنید یک دوربین مداربسته در حال ضبط شماست، این کار باعث خودآگاهی در لحظه خشم می‌شود.",
            categories = setOf("anger"),
            minLevel = StressLevel.MODERATE,
            maxLevel = StressLevel.HIGH
        ),
        AdviceItem(
            id = "anger_book",
            title = "کتاب: مدیریت خشم",
            body = "یادگیری تکنیک‌های علمی برای کنترل هیجانات شدید در لحظه.",
            categories = setOf("anger"),
            minLevel = StressLevel.LOW,
            maxLevel = StressLevel.LOW
        ),
        AdviceItem(
            id = "anger_nature_sounds",
            title = "گوش دادن به صدای طبیعت",
            body = "صداهای باران یا امواج دریا می‌توانند ضربان قلب را کاهش دهند.",
            categories = setOf("anger", "sleep"),
            minLevel = StressLevel.LOW,
            maxLevel = StressLevel.MODERATE
        ),
        // === SLEEP ===
        AdviceItem(
            id = "sleep_digital_detox",
            title = "دیتاکس دیجیتال قبل از خواب",
            body = "یک ساعت قبل از خواب، تمامی صفحات نمایش (گوشی، لپ‌تاپ) را خاموش کنید.",
            categories = setOf("sleep"),
            minLevel = StressLevel.LOW,
            maxLevel = StressLevel.MODERATE
        ),
        AdviceItem(
            id = "sleep_book_why_we_sleep",
            title = "کتاب: چرا می‌خوابیم (متیو واکر)",
            body = "یادگیری اهمیت خواب و تاثیر آن بر سلامت روان و جسم.",
            categories = setOf("sleep"),
            minLevel = StressLevel.LOW,
            maxLevel = StressLevel.LOW
        ),
        AdviceItem(
            id = "sleep_cool_dark_room",
            title = "محیط تاریک و خنک",
            body = "دمای اتاق خواب را خنک نگه دارید (حدود ۱۸-۲۰ درجه) و اتاق را کاملا تاریک کنید.",
            categories = setOf("sleep"),
            minLevel = StressLevel.LOW,
            maxLevel = StressLevel.MODERATE
        ),
        AdviceItem(
            id = "sleep_asmr",
            title = "پادکست‌های ASMR",
            body = "گوش دادن به پادکست‌های آرام‌بخش می‌تواند شما را برای خواب آماده کند.",
            categories = setOf("sleep"),
            minLevel = StressLevel.LOW,
            maxLevel = StressLevel.MODERATE
        ),
        AdviceItem(
            id = "sleep_pmr",
            title = "تکنیک آرام‌سازی پیشرونده عضلانی",
            body = "تمام عضلات بدن را منقبض کرده و سپس آرام رها کنید، از نوک پا تا سر.",
            categories = setOf("sleep", "anxiety"),
            minLevel = StressLevel.MODERATE,
            maxLevel = StressLevel.HIGH,
            videoUrl = "https://www.youtube.com/watch?v=1nZEdqcGVzo"
        ),
        AdviceItem(
            id = "sleep_warm_milk",
            title = "نوشیدن شیر گرم",
            body = "شیر گرم حاوی تریپتوفان است که به تولید ملاتونین (هورمون خواب) کمک می‌کند.",
            categories = setOf("sleep"),
            minLevel = StressLevel.LOW,
            maxLevel = StressLevel.MODERATE
        ),
        AdviceItem(
            id = "sleep_todo_list",
            title = "یادداشت برنامه‌های فردا",
            body = "نوشتن کارهای فردا، ذهن را از پردازش مداوم آنها در طول شب آزاد می‌کند.",
            categories = setOf("sleep", "anxiety"),
            minLevel = StressLevel.MODERATE,
            maxLevel = StressLevel.HIGH
        ),
        AdviceItem(
            id = "sleep_20min_rule",
            title = "استفاده از قانون ۲۰ دقیقه",
            body = "اگر پس از ۲۰ دقیقه خوابتان نبرد، از تخت خارج شوید و کاری آرام‌بخش انجام دهید.",
            categories = setOf("sleep"),
            minLevel = StressLevel.MODERATE,
            maxLevel = StressLevel.HIGH
        ),
        // === BURNOUT ===
        AdviceItem(
            id = "burnout_pomodoro",
            title = "تکنیک پومودورو",
            body = "۲۵ دقیقه کار و ۵ دقیقه استراحت. این روش از خستگی ذهنی جلوگیری می‌کند.",
            categories = setOf("burnout", "exam_stress"),
            minLevel = StressLevel.LOW,
            maxLevel = StressLevel.MODERATE,
            videoUrl = "https://www.aparat.com/v/Z4M1m"
        ),
        AdviceItem(
            id = "burnout_80_20",
            title = "قانون ۸۰/۲۰",
            body = "تمرکز بر ۲۰ درصد از کارهایی که ۸۰ درصد نتایج را به همراه دارند.",
            categories = setOf("burnout"),
            minLevel = StressLevel.LOW,
            maxLevel = StressLevel.MODERATE
        ),
        AdviceItem(
            id = "burnout_say_no",
            title = "هنر نه گفتن",
            body = "یاد بگیرید به کارهای غیرضروری که انرژی شما را می‌گیرند نه بگویید.",
            categories = setOf("burnout"),
            minLevel = StressLevel.MODERATE,
            maxLevel = StressLevel.HIGH
        ),
        AdviceItem(
            id = "burnout_nature",
            title = "طبیعت‌گردی کوتاه",
            body = "گذراندن وقت در فضاهای سبز سطح کورتیزول (هورمون استرس) را کاهش می‌دهد.",
            categories = setOf("burnout", "joy"),
            minLevel = StressLevel.MODERATE,
            maxLevel = StressLevel.HIGH
        ),
        AdviceItem(
            id = "burnout_boundaries",
            title = "تعیین مرزهای کاری",
            body = "بعد از ساعت کاری، ایمیل‌ها و پیام‌های کاری را بررسی نکنید.",
            categories = setOf("burnout"),
            minLevel = StressLevel.MODERATE,
            maxLevel = StressLevel.HIGH
        ),
        AdviceItem(
            id = "burnout_digital_leave",
            title = "مرخصی دیجیتال",
            body = "یک روز کامل در هفته را بدون اینترنت و گوشی موبایل سپری کنید.",
            categories = setOf("burnout"),
            minLevel = StressLevel.MODERATE,
            maxLevel = StressLevel.HIGH
        ),
        AdviceItem(
            id = "burnout_break_tasks",
            title = "تقسیم کارهای بزرگ",
            body = "کارهای بزرگ را به قدم‌های بسیار کوچک و قابل مدیریت تقسیم کنید.",
            categories = setOf("burnout", "exam_stress"),
            minLevel = StressLevel.MODERATE,
            maxLevel = StressLevel.HIGH
        ),
        AdviceItem(
            id = "burnout_deep_work",
            title = "کتاب: کار عمیق (کال نیوپورت)",
            body = "یادگیری روش‌های تمرکز عمیق بدون حواس‌پرتی.",
            categories = setOf("burnout"),
            minLevel = StressLevel.LOW,
            maxLevel = StressLevel.LOW
        ),
        AdviceItem(
            id = "burnout_celebrate_small",
            title = "جشن گرفتن بردهای کوچک",
            body = "حتی تمام کردن یک کار کوچک را جشن بگیرید تا مغزتان پاداش دریافت کند.",
            categories = setOf("burnout", "joy"),
            minLevel = StressLevel.LOW,
            maxLevel = StressLevel.MODERATE
        ),
        // === JOY ===
        AdviceItem(
            id = "joy_kindness",
            title = "گسترش مهربانی",
            body = "امروز به یک دوست پیام بدهید و از او بابت حضورش در زندگی‌تان تشکر کنید.",
            categories = setOf("joy"),
            minLevel = StressLevel.LOW,
            maxLevel = StressLevel.LOW
        ),
        AdviceItem(
            id = "joy_new_skill",
            title = "یادگیری یک مهارت جدید",
            body = "حالا که ذهن آرامی دارید، یک کار جدید (مثل نقاشی یا یک زبان جدید) را شروع کنید.",
            categories = setOf("joy"),
            minLevel = StressLevel.LOW,
            maxLevel = StressLevel.LOW
        ),
        AdviceItem(
            id = "joy_playlist",
            title = "ساختن یک پلی‌لیست شاد",
            body = "آهنگ‌های مورد علاقه‌تان که انرژی مثبت می‌دهند را در یک لیست جمع کنید.",
            categories = setOf("joy"),
            minLevel = StressLevel.LOW,
            maxLevel = StressLevel.LOW
        ),
        AdviceItem(
            id = "joy_advanced_mindfulness",
            title = "تمرین ذهن‌آگاهی پیشرفته",
            body = "از این آرامش استفاده کنید تا مدیتیشن‌های طولانی‌تر و عمیق‌تری را تجربه کنید.",
            categories = setOf("joy"),
            minLevel = StressLevel.LOW,
            maxLevel = StressLevel.LOW
        ),
        AdviceItem(
            id = "joy_positive_journal",
            title = "خاطره‌نویسی مثبت",
            body = "خاطرات شیرین روزهای اخیر را با جزئیات کامل بنویسید تا تثبیت شوند.",
            categories = setOf("joy"),
            minLevel = StressLevel.LOW,
            maxLevel = StressLevel.LOW
        ),
        AdviceItem(
            id = "joy_dance",
            title = "رقصیدن در خانه",
            body = "با آهنگ مورد علاقه‌تان برقصید، این کار اندورفین زیادی تولید می‌کند.",
            categories = setOf("joy"),
            minLevel = StressLevel.LOW,
            maxLevel = StressLevel.LOW
        ),
        AdviceItem(
            id = "joy_cook",
            title = "پختن یک غذای جدید",
            body = "آشپزی آگاهانه و خلق یک مزه جدید، لذت زیادی به همراه دارد.",
            categories = setOf("joy"),
            minLevel = StressLevel.LOW,
            maxLevel = StressLevel.LOW
        ),
        // === HIGH STRESS FALLBACK ===
        AdviceItem(
            id = "high_stress_support",
            title = "ارتباط با یک فرد مورداعتماد",
            body = "اگر فشار زیاد است، امروز با یک دوست، عضو خانواده یا متخصص قابل اعتماد صحبت کن.",
            categories = setOf(
                "anxiety",
                "sleep",
                "burnout",
                "anger",
                "exam_stress",
                "depression"
            ),
            minLevel = StressLevel.HIGH,
            maxLevel = StressLevel.HIGH,
            requiresProfessionalSupport = true
        )
    )

    fun getAdviceForCategory(
        category: String,
        likedAdviceIds: List<String> = emptyList()
    ): List<AdviceItem> {
        val categoryAdvice = adviceList.filter {
            category in it.categories
        }

        return categoryAdvice.sortedByDescending {
            it.id in likedAdviceIds
        }
    }
}

