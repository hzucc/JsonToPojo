# JsonToPojo
我的javaUtil包

MyJsonObject工具类:将一个JSON字符串转换成为PoJo（java对象）或转换为java数组。

拉取代码，执行MyJsonObjectTest（测试类），可以看到一个测

时间复杂度为线性。

使用对象里的setXXX反射方法映射一个json里的字段，而不是对象的字段映射json字段，所以可以在setXXX方法里进行细节操作。

所以一个json字符串反射成为的对象的形态，取决于自定义的对象及其内部的setXXX方法。试的结果。


......

详细描述参见项目里的： 设计解析JSON为Object的算法文档   

文档里还包含有测试样例
