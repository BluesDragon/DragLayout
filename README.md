#DragLayout
基于`BlueMor`的`DragLayout`二次开发, 简化其`使用`过程. 在原来的基础上支持代码生成
由于实现的效果没有变化,So.图我也就没有换了.

![](https://github.com/BlueMor/DragLayout/raw/master/screenshots/123.gif)


#Import

* `supportV4`
* `nineoldandroids`

#Change Log
* 修改其滑动范围可控
* 在原来的基础上支持代码生成
* 修改将其主布局控制放入DragLayout，不再限制于原来的MyRelativeLayout
* 移除了原来的shadowImageView

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
[黎稀](http://www.cxh.name/)

#Contact me
lixi0912@gmail.com

#License
```
Copyright (c) 2014 Anton Malinskiy

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
Come on, don't tell me you read that.
```
