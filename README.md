# custom view of calendar

## 第一步
 New --> import Module   导入cal模块
 
## 第二步
 app/build.gradle
 ```groovy
 compile project(':cal')
 ```
 
 
## 第三步
```xml
    <com.k.cal.CalendarLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:background="#FFD700"
        android:orientation="vertical"
        >

        <com.k.cal.CalendarView
            android:id="@+id/calendar"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            />

        <CheckBox
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:layout_gravity="center_horizontal"
            />

    </com.k.cal.CalendarLayout>
```

## preview
![image](https://github.com/Am-Kc/cal/raw/b_readme/app/calendar.gif)
