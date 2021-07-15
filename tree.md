
├── demo-ktx
├── keystore.properties
├── library_base
│   ├── libs
│   │   └── flyfun_support_1.0.4.jar
│   └── src
│       └── main
│           ├── AndroidManifest.xml
│           └── java
│               └── cn
│                   └── flyfun
│                       └── gamesdk
│                           └── base
│                               ├── FlyFunGame.kt
│                               ├── FlyFunGameApplication.kt
│                               ├── SdkBridgeManager.kt
│                               ├── entity
│                               │   ├── FunctionName.kt
│                               │   ├── GameChargeInfo.kt
│                               │   └── GameRoleInfo.kt
│                               ├── internal
│                               │   └── ICallback.kt
│                               └── utils
│                                   ├── Logger.kt
│                                   └── ParamsUtils.kt
├── library_core
│   ├── libs
│   │   └── flyfun_base_1.1.7.jar
│   ├── src
│   │   └── main
│   │       ├── java
│   │       │   └── cn
│   │       │       └── flyfun
│   │       │           └── gamesdk
│   │       │               ├── Version.kt
│   │       │               └── core
│   │       │                   ├── SdkBridge.kt
│   │       │                   ├── entity
│   │       │                   │   ├── ClickType.kt
│   │       │                   │   ├── GameRewardInfo.kt
│   │       │                   │   ├── LoginType.kt
│   │       │                   │   ├── ResultInfo.kt
│   │       │                   │   ├── SdkBackLoginInfo.kt
│   │       │                   │   ├── Session.kt
│   │       │                   │   └── bean
│   │       │                   │       ├── InitBean.kt
│   │       │                   │       └── LogBean.kt
│   │       │                   ├── fama
│   │       │                   │   ├── EventSubject.kt
│   │       │                   │   └── channel
│   │       │                   │       ├── adjust
│   │       │                   │       │   ├── AdjustEventBean.kt
│   │       │                   │       │   └── AdjustImpl.kt
│   │       │                   │       └── firebase
│   │       │                   │           └── FirebaseImpl.kt
│   │       │                   ├── impl
│   │       │                   │   ├── BindActivity.kt
│   │       │                   │   ├── HybridActivity.kt
│   │       │                   │   ├── SdkBridgeImpl.kt
│   │       │                   │   ├── iab
│   │       │                   │   │   ├── ChargeImpl.kt
│   │       │                   │   │   ├── InAppBilling.kt
│   │       │                   │   │   └── PreRewardImpl.kt
│   │       │                   │   └── login
│   │       │                   │       ├── LoginActivity.kt
│   │       │                   │       ├── LoginFragmentPagerAdapter.kt
│   │       │                   │       ├── SignInImpl.kt
│   │       │                   │       └── fragment
│   │       │                   │           ├── ChooseFragment.kt
│   │       │                   │           └── RegisterFragment.kt
│   │       │                   ├── internal
│   │       │                   │   ├── IEventObserver.kt
│   │       │                   │   ├── IFileRequestCallback.kt
│   │       │                   │   ├── IRequestCallback.kt
│   │       │                   │   └── ImplCallback.kt
│   │       │                   ├── network
│   │       │                   │   ├── Host.kt
│   │       │                   │   ├── SdkJsImpl.kt
│   │       │                   │   ├── SdkRequest.kt
│   │       │                   │   ├── VolleyRequest.kt
│   │       │                   │   └── VolleySingleton.kt
│   │       │                   ├── ui
│   │       │                   │   ├── DialogUtils.kt
│   │       │                   │   ├── EventEditText.kt
│   │       │                   │   ├── VerifyCodeEditText.kt
│   │       │                   │   └── dialog
│   │       │                   │       ├── InitDialog.kt
│   │       │                   │       ├── PrivacyDialog.kt
│   │       │                   │       └── TipsDialog.kt
│   │       │                   └── utils
│   │       │                       ├── AndroidBug5497Workaround.kt
│   │       │                       ├── FileUtils.kt
│   │       │                       ├── SPUtils.kt
│   │       │                       ├── ScreenShotUtils.kt
│   │       │                       ├── SessionUtils.kt
│   │       │                       └── TimeDownUtils.kt
│   │       ├── jniLibs
│   │       │   ├── arm64-v8a
│   │       │   │   └── libflyfun_drive.so
│   │       │   ├── armeabi-v7a
│   │       │   │   └── libflyfun_drive.so
│   │       │   ├── x86
│   │       │   │   └── libflyfun_drive.so
│   │       │   └── x86_64
│   │       │       └── libflyfun_drive.so
│   │       └── res
├── local.properties
└── zkeystore
