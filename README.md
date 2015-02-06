#DragLayout
基于`BlueMor`的`DragLayout`二次开发, 简化其`使用`过程. 支持在代码中直接使用
由于实现的效果没有变化,So.图我也就没有换了.

![](https://github.com/BlueMor/DragLayout/raw/master/screenshots/123.gif)


#Import

* `supportV4`
* `nineoldandroids`

#Usage
* 在代码中使用 
```java
setContentView(R.id.xx)
DragLayout layout = new DragLayout(this);
final SlideMenu menu = new SlideMenu(this);//侧滑菜单
layout.updateRangeRatio(0.5f);//滑动范围
layout.setBackgroundResource(R.drawable.icon_bg_slide);//主背景
layout.attachToActivity(this, menu);//after setBackground
layout.setOnDragStateListener(new OnDragStateListener() {

      @Override
      public void onOpen() {
      }
      
      @Override
      public void onDrag(float percent) {
      }
      
      @Override
      public void onClose() {
      }
});
```
* 在XML中使用
```
  <DragLayout>
  
    <!--SlideMenu Layout -->
    <ViewGroup>
    </ViewGroup>
    
    <!-- Main Layout -->
    <ViewGroup>
    </Viewgroup>
    
  </DragLayout>
```
#Thanks
Developers @[BlueMor](https://github.com/BlueMor) [DragLayout](https://github.com/BlueMor/DragLayout "原项目地址")

#About me
黎稀

#Contact me
lixi0912@gmail.com

#License
The MIT License (MIT)

Copyright (c) 2015 iluos

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
