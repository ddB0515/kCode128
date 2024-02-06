# kCode128
Kotlin implementation of Code128 barcode to be used as ImageView + simple demo application

<a href='https://play.google.com/store/apps/details?id=ddb0515.kcode128&pcampaignid=pcampaignidMKT-Other-global-all-co-prtnr-py-PartBadge-Mar2515-1'><img width="300" height="100" alt='Get it on Google Play' src='https://play.google.com/intl/en_us/badges/static/images/badges/en_badge_web_generic.png'/></a>

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
