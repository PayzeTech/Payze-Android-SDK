# Payze-Android-SDK
Payze Android SDK for no-redirect payments

# Installation
Add it in your root build.gradle at the end of repositories:
```bash
dependencyResolutionManagement {
  repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
  repositories {
    mavenCentral()
    maven { url 'https://jitpack.io' }
  }
}
```

Add a dependency to our library in the app build.gradle file:
```bash
dependencies {
  implementation 'com.github.PayzeTech:Payze-Android-SDK:1.0.0'
}
```

# Usage:
Just create instance of Payze class with context and call method 'init()' in onCreate(). Then call method 'start(...)'. This method takes language, transactionId, amount data, company logo resource, environment & result callback as parameters.

```bash
private val payze = Payze(requireContext())

override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        payze.init()
}

payze.start(
    language = Language.EN,
    transactionId = "add transaction id",
    companyLogoRes = R.drawable.company_logo,
    amount = Money(
        amount = 12.0,
        currency = Currency.SUM
    ),
    environment = ServiceEnvironment.PRODUCTION,
    onResult = {
        when (it) {
           PayzeResult.IN_PROGRESS -> "Add in progress logic")
           PayzeResult.SUCCESS -> "Add success logic")
           PayzeResult.FAIL -> "Add failure logic")
        }
    }
)
```

## Language:
Our SDK supports 3 languages.

```bash
@Parcelize
enum class Language(val prefix: String): Parcelable {
    EN("en"),
    RU("ru"),
    UZ("uz"),
}
```

## Amount data:
```bash
@Parcelize
data class Money(
    val amount: Double,
    val currency: Currency
): Parcelable

@Parcelize
enum class Currency(val value: String): Parcelable {
    USD("USD"),
    SUM("SUM")
}
```

## Environment:
```bash
@Parcelize
enum class ServiceEnvironment: Parcelable {
    DEVELOPMENT,
    PRODUCTION
}
```

## Result callback:
```bash
enum class PayzeResult(val value: Int) {
    IN_PROGRESS(1),
    SUCCESS(2),
    FAIL(3);

    companion object {
        fun fromValue(id: Int?): PayzeResult {
            for (type in values()) {
                if (type.value == id) {
                    return type
                }
            }
            return IN_PROGRESS
        }
    }
}
```
