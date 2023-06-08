# Money Tracker App
Application that lets you track your expenses, set expense limits, add income and save expense info to aa file.

## Technology Stack
* Koltin
* Jetpack Compose (UI)
* Room (database)
* Retrofit (HTTP REST API)
* MVVM
* JUnit
## App Overview
### Home Screen. Expenses can be viewed on a certain date(daily, monthly, weekly), as list or as stats (with diagrams, limits/income if present). Example:


<img width="360" alt="home_daily" src="https://github.com/Kirillbiliashov/MoneyTrackerApp/assets/81979605/977be541-1a96-4f0c-b4f0-e65c9e1df35d"> <img width="358" alt="home_weekly" src="https://github.com/Kirillbiliashov/MoneyTrackerApp/assets/81979605/d538f6a1-acae-4372-a94e-ecf58b5c38b0">

<img width="351" alt="home_bar_chart" src="https://github.com/Kirillbiliashov/MoneyTrackerApp/assets/81979605/9a07bc3f-8c3e-464c-b4ee-a009a440ed25"> <img width="348" alt="home_pie_chart" src="https://github.com/Kirillbiliashov/MoneyTrackerApp/assets/81979605/32cfe7a0-d6c9-404c-8d78-0f32bb9f8955">

<img width="358" alt="home_progress_bar" src="https://github.com/Kirillbiliashov/MoneyTrackerApp/assets/81979605/30c951a3-788a-43f7-9103-92ab88f00ae6">

### Categories screen. You can view categories, select/unselct them, and add new ones using dialog
<img width="348" alt="categories_list" src="https://github.com/Kirillbiliashov/MoneyTrackerApp/assets/81979605/481a0c0b-8fc8-4a54-aa83-c6aad86868d9"> <img width="358" alt="categories_dialog" src="https://github.com/Kirillbiliashov/MoneyTrackerApp/assets/81979605/e5881e4f-797c-4b70-9839-8ebac179179c">

### Expense Screen. The form for registering new expense
<img width="364" alt="expense_form" src="https://github.com/Kirillbiliashov/MoneyTrackerApp/assets/81979605/65ee1673-7105-409c-8c5f-c25de79e497b">

### Settings screen. Limits and income history can be viewed here, as well as dialogs for adding new entries (like for categories). After file is saved, notification appears
<img width="351" alt="settings" src="https://github.com/Kirillbiliashov/MoneyTrackerApp/assets/81979605/01188f93-7e0f-42c9-acc4-57ab3e087065"> <img width="343" alt="settings_save_file" src="https://github.com/Kirillbiliashov/MoneyTrackerApp/assets/81979605/c63de305-b201-488a-8d57-41c8c4fab8c8">

## How to run

* Clone repository to your local machine: `git clone https://github.com/Kirillbiliashov/MoneyTrackerApp.git`
* Proceed to project dir and run the following command: `./gradlew build`. If you don't have gradle installed,
 proceed to this [guide](https://gradle.org/install/)
* If you want to run UI tests, use this command: `./gradlew connectedAndroidTest `. Keep in mind that they are run using android device, so you need to connect your own device or use emulator. 
* You can also run [apk file](https://github.com/Kirillbiliashov/MoneyTrackerApp/raw/master/apk/money_tracker.apk) directly. If you don't know how to do this, proceed to this [guide](https://www.javatpoint.com/how-to-install-apk-on-android).



