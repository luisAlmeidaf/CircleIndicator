# CircleIndicator

This design is a circular indicator to be used in the recycler view, 
just like those used in ViewPagers, and was based in the lib [CircleIndicator](https://github.com/ongakuer/CircleIndicator).

The main goals of this personal project were:
* Isolate code referring to recyclerview.
* Migrate Java code to Kotlin, following the Clean Code guidelines as much as possible.
* Allow control of height and width of selected and deselected indicators alone.

<img src="circle_indicator.gif" width="280">

## Usage:

### Part 1 - Installation: 
```
allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```

```
dependencies {
	        implementation 'com.github.luisAlmeidaf:CircleIndicator:v1.0.2'
	}
```

### Part 2 - XML Example of usage: 

```
<?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.widget.ConstraintLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    tools:context=".MainActivity">

    <androidx.recyclerview.widget.RecyclerView
        android:id="@+id/rv_image_generic"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:background="@android:color/white"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toTopOf="parent" />

    <com.luisfalmeida.circleindicator.circleindicator.CircleIndicator
        android:id="@+id/indicator"
        android:layout_width="match_parent"
        android:layout_height="48dp"
        android:background="@color/white"
        app:ci_width="12dp"
        app:ci_height="4dp"
        app:ci_deselected_width="4dp"
        app:ci_deselected_height="4dp"
        app:ci_drawable="@drawable/ic_dot_black"
        app:ci_drawable_unselected="@drawable/ic_dot_gray"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintTop_toBottomOf="@+id/rv_image_generic"
        app:layout_constraintStart_toStartOf="parent" />

</androidx.constraintlayout.widget.ConstraintLayout>
```

### Part 3: Create recyclerview and circleIndicator

```
val recyclerView = findViewById(R.id.rv_image_gallery)
val circleIndicator = findViewById(R.id.indicator)
```

### Part 4: Create and attach Snap Helper

```
pagerSnapHelper = PagerSnapHelper()
pagerSnapHelper.attachToRecyclerView(recyclerView)
```

### Part 5: Set recyclerview

```
recyclerView.apply {
  this.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
  adapter = imagesAdapter
  circleIndicator.attachToRecyclerView(this, pagerSnapHelper)
}
```

### Properties

| Properties | Default Value |
| ---------- | ------------- |
app:ci_width	| 5dp
app:ci_height	| 5dp
app:ci_deselected_width	| 4dp
app:ci_deselected_height	| 4dp
app:ci_margin	| 5dp
app:ci_drawable	| R.drawable.white_radius
app:ci_drawable_unselected |	R.drawable.white_radius
app:ci_animator |	R.animator.scale_with_alpha
app:ci_animator_reverse |	0
app:ci_orientation | horizontal
app:ci_gravity | center

This is an ongoing project, if you have any criticisms or suggestions, please leave an issue here, or contact me via email: luis.almeida.filipe@gmail.com


