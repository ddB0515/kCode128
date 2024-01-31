# kCode128
Kotlin implementation of Code128 barcode to be used as ImageView + simple demo application


## Screenshot

<img src="https://github.com/ddb0515/kCode128/raw/master/Screenshot.png" data-canonical-src="https://github.com/ddb0515/kCode128/raw/master/Screenshot.png" width="300" height="500"  alt="image"/>

## Usage

In your XML use it as: 

```
<ddb0515.kcode128.kCode128
android:id="@+id/barcodeView"
android:layout_width="250dp"
android:layout_height="150dp"/>
```

and after from code simple use it as shown in MainActivity.kt

```
barcodeView.setData("1234567890ABCDEF")
```

Hope you will find it helpful 
Enjoy :)
