dundee
==============
[![CircleCI](https://circleci.com/gh/strvcom/dundee.svg?style=svg)](https://circleci.com/gh/strvcom/dundee)

<p align="center">
	<img src="https://github.com/strvcom/dundee/raw/master/extras/device-2018-02-26-134516.png" width="320"/>
	<img src="https://github.com/strvcom/dundee/raw/master/extras/device-2018-02-26-135801.png" width="320"/>
</p


Dundee is a showcase app written to demonstrate modern Android app architecture approaches. It displays current value of various cryptocurrencies fetched from different sources, 
stores your portfolio in the cloud within Firebase Firestore database and provides you with a detailed info about profit/loss for each of the currencies.

The app leverages MVVM architecture provided by Android Architecture Components. 
Together with Kotlin language features and Android Data Binding framework there is a minimal amount of boilerplate code needed to create fully functional app. 
Data is accessed through a repository pattern, network call results can be automatically cached into database via Room library. The architecture also uses a simple dependency injection
mechanism which provides all you need to make your classes testable and mockable. See the `com.strv.ktools` package for all tools. Key tools are described below.

MVVM Architecture via ViewModelBinding
----------------

ViewModelBinding (vmb) connects Architecture ViewModels with the View (Activity/Fragment) via Data Binding automatically. All you have to do is to initialize it in your View via Kotlin delegate:

```kotlin
private val vmb by vmb<MainViewModel, ActivityMainBinding>(R.layout.activity_main) 
//...
// then in onCreate():
vmb.viewModel.doSomething()
vmb.binding.myView.doSomethingElse()
```

_see MainView.kt class for more_

View Class (Activity/Fragment) does not have to implement or extend anything special. In case of a Fragment, you still need to override the `onCreateView()` method though. But you can just return `vmb.rootView`.
ViewModel needs to extend Architecture Components `ViewModel` or `AndroidViewModel`. 

If you want to instantiate ViewModel yourself, you can provide a lambda functions that provides the instance. 
This function will be called just in time when you have access to Intent and other data within the Activity/Fragment so you can pass any parameters to your ViewModel constructor:

```kotlin
private val vmb by vmb<SignUpViewModel, ActivitySignUpBinding>(R.layout.activity_sign_up) { SignUpViewModel(intent.getStringExtra(EXTRA_DEFAULT_EMAIL), intent.getStringExtra(EXTRA_DEFAULT_PASSWORD)) }
```

ViewModels hold data exclusively within LiveData. ObservableFields were replaced by MutableLiveData which can be directly consumed by the View. Once you use LiveData instance within a layout file DataBinding mechanism will observe 
the data with a proper LifecycleOwner - Activity or Fragment.

View communicates with ViewModel directly via public API. ViewModels communicate with View via LiveData or EventLiveData - see `SignUpViewModel` for an example

Dependency Injection
--------------------

We use basic DI mechanism consisting of two kotlin functions. First you need to call `provide { Gson() }` or `provideSingleton { Gson() }` somewhere (see `DIModule.kt`) and then to inject the dependency into a property
just use `val gson by inject<Gson>()`. You can also use different scopes for the DI by providing String scope name to both functions.

In your test suite you can use different module which will provide different or mock variants of your classes.

Repository Pattern
------------------

Repository pattern used in this project is based on [this article](https://developer.android.com/topic/libraries/architecture/guide.html) but heavily modified. The idea is that both network calls and database operations
return data in form of LiveData. NetworkBoundResource takes care of merging those two data sources into single LiveData using `MediatorLiveData`. It can manage both simple (not cached) network calls as well as automatically cached calls.
Data is always wrapped within a `Resource` class adding status, message and potentially a Throwable instance. See `repository.kt` for more and `DashboardViewModel` with `TickerLiveData` for an example.

To be able to receive data from REST API within a LiveData there is a custom Retrofit `CallAdapterFactory` which transforms `Call<T>` to `LiveData<Resource<T>>`

Shared Preferences Delegate
---------------------------
Shared Preferences Delegates massively simplifies work with Android SharedPreferences. Just declare a property using the delegate and read/set its value. It will be automatically stored within SharedPreferences of your choice.
Example:

```kotlin
var accessToken by sharedPrefs().string() // property name will be used as the key
var userEmail by sharedPrefs().string(key = "email")
var hasReadConditions by sharedPrefs().boolean(false)
//...
// then
accessToken = "asd328y0823hdkajshd238"
if (!hasReadConditions){
	//...
	hasReadConditions = true
}
```

If you rather work with LiveData, there are delegates for that as well. When active, the LiveData listens to SharedPreferences changes for the given key and gets updated automatically. It supports setting value as well so
you can connect it to the layout directly:

```kotlin
// in ViewModel
var userName by sharedPrefs().stringLiveData()
```

```xml
<!-- in layout -->
<EditText android:text="@={viewModel.userName}"/>
```

Other Tools
-----------
**Logs** - multiple logging functions - see `log.kt`

**LiveData** - `LiveData.map()`, `LiveData.switchMap()` extension shorthands, `EventLiveData` for delivering one-time events to View, `mutableLiveDataOf()` shorthand, etc. - see `livedata.kt`

**Async** - `doAsync {}` and `uiThread {}` functions to simplify background operations
	

Author
------

<p align="center">
	<a href="http://www.strv.com">
		<img src="https://github.com/strvcom/dundee/raw/master/extras/strv-logo.png" width="320"/>
	</a>
	<br/>
	<b>Jakub Kinst</b> (jakub.kinst@strv.com)
	<br/>
	<b>Leos Dostal</b> (leos.dostal@strv.com)
</p>