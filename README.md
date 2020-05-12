[![](https://jitpack.io/v/noowenz/ShowMoreLess.svg)](https://jitpack.io/#noowenz/ShowMoreLess)[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)[![Android Arsenal]( https://img.shields.io/badge/Android%20Arsenal-ShowMoreLess-green.svg?style=flat )]( https://android-arsenal.com/details/1/8087 )

# ShowMoreLess

ShowMoreLess is a A simple Android library for displaying a more and less functionality in TextView 

## Screenshots

<img src="https://github.com/noowenz/ShowMoreLess/blob/master/art/showmore.png" width="250px" />
<img src="https://github.com/noowenz/ShowMoreLess/blob/master/art/showless.png" width="250px" />

## Installation

Add Jitpack to your project build.gralde file
      
```Kotlin
allprojects {
   repositories {
      ...
      maven { url 'https://jitpack.io' }
   }
}
```

Then add this dependency to your app build.gradle file.

```Kotlin
dependencies {
   implementation 'https://github.com/noowenz/ShowMoreLess:latest-release'
   Or
   implementation 'com.github.noowenz:ShowMoreLess:v1.0'
}
```

## Usage

```Kotlin
ShowMoreLess.Builder(this)
   /*.textLengthAndLengthType(
     	length = 100,
        textLengthType = ShowMoreLess.TYPE_CHARACTER
   )*/
   .textLengthAndLengthType(
   	length = 5,
        textLengthType = ShowMoreLess.TYPE_LINE
   )
   .showMoreLabel("show more")
   .showLessLabel("show less")
   .showMoreLabelColor(R.color.colorPrimaryDark)
   .showLessLabelColor(R.color.colorPrimaryDark)
   .labelUnderLine(labelUnderLine = false)
   .expandAnimation(expandAnimation = true)
   .textClickable(
   	textClickableInExpand = true,
        textClickableInCollapse = true
   )
   .build().apply {
   	addShowMoreLess(textView = tv_first, text = tv_first.text, isContentExpanded = true)
        setListener(object : ShowMoreLess.OnShowMoreLessClickedListener {
	
             override fun onShowMoreClicked() {
             	//We can handle or save show more state
             }

             override fun onShowLessClicked() {
             	//We can handle or save show less state
             }
   	}
   )
}	
```

# Customization 
## You can customize things like bellow

##### 1. Can change *textLengthAndLengthType* as

```Kotlin
.textLengthAndLengthType(
     length = 100,//Length where to add show more text
     textLengthType = ShowMoreLess.TYPE_CHARACTER //textLengthType is TYPE_CHARACTER
)
    and
.textLengthAndLengthType(
     length = 5,//Line of text view where to add show more text
     textLengthType = ShowMoreLess.TYPE_LINE //textLengthType is TYPE_LINE
)
```

##### 2. Can change *show more* and *show less* text label as

```Kotlin
.showMoreLabel("read more")
.showLessLabel("read less")
```

##### 3. Can change text *color* of *Show more* or *Show less*

```Kotlin
.showMoreLabelColor(Color.parseColor("#ffffff"))
.showLessLabelColor(Color.parseColor("#ffffff"))
```

##### 4. Can enable *labelUnderLine* easily as

```Kotlin
.labelUnderLine(labelUnderLine = false)//true for underline and false for not underline
```

##### 5. Can easily handle expand and collapse *animation* by

```Kotlin
.expandAnimation(expandAnimation = true)//expandAnimation will affect both expand and collapse logic
```

##### 6. Can handle *textClickable* except clicking more or less txt for expand and collapse

```Kotlin
.textClickable(
     textClickableInExpand = true,//It will enable text clickable in expand mode
     textClickableInCollapse = true//It will enable text clickable in collapse mode
)
```

##### 7. *addShowMoreLess()* function will handle text expand or collapse state

```Kotlin
addShowMoreLess(
     textView = tv_first, //It is a text view where we have to add read more or less
     text = tv_first.text, //String in textview
     isContentExpanded = true//Content expand or collapse history
)
```

##### 8. At last *setListener()* function for callbacks

```Kotlin
setListener(object : ShowMoreLess.OnShowMoreLessClickedListener {

     override fun onShowMoreClicked() {
     	//We can handle or save show more state
     	//This sate will send in no 7.addShowMoreLess() function of isContentExpanded = false / true
     }

     override fun onShowLessClicked() {
     	//We can handle or save show less state
     	//This sate will send in no 7.addShowMoreLess() function of isContentExpanded = false / true
     }
}
```
 

## License

Copyright (c) 2020 Nabin Shrestha

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at
         
        http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
