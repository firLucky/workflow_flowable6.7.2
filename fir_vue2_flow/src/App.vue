<template>
  <div id="app">
    <router-view/>
  </div>
</template>

<script>

export default {
  name: 'App',
  components: {
  }
}
</script>

<style>

html {
  /* px 表示 “像素（pixels）”: 基础字号为 10 像素 */
  /*默认大小为1920*1080*/
  width: 100%;
  height: 100%;

  /*width: 1920px;*/
  /*height: 1080px;*/
  font-size: 10px; /*设置字体尺寸*/
  /* Google fonts 输出的 CSS */
  font-family: 'Open Sans', sans-serif;
  /*更改页面颜色*/
  /*background-color: #dca463;*/
  /*background-color: #ffffff;*/
  background-color: rgb(83, 91, 100);
  /*background-size: 1920px 1080px;  !*设置背景图片大小*!*/
  background-size: 100% 100%;  /*设置背景图片大小*/
  transition: .2s linear;  /*css过度伪切换效果*/
  overflow: auto; /*元素的裁剪显示*/
}

#app {
  width: 100%;
  height: 100%;
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  color: #2c3e50;
}

nav {
  padding:0 5px 15px 20px;  /*元素的 4 个内边距：*/
  width: 200px;
  float: left;
  /*height: 32px;*/
  /*margin-left:800px;  !*设置外边距左边界距离*!*/
}
nav a {
  font-weight: bold;
  color: #2c3e50;
}

nav a {
  color: #42b983;
  padding: 0;
}
body{
  height: 100%;
  padding: 0;
  margin:0;              /*清除外边距*/
}

#rightLink{
  float: left;
  width: 80px;
}
/* 解决element-ui的table表格控件表头与内容列不对齐问题 */
/*body .el-table th.gutter{*/
/*  display: table-cell!important;*/
/*}*/
/*.el-table th.gutter {*/
/* display: table-cell !important;*/
/*}*/
/*   .el-table__header col[name="gutter"] {*/
/*     display: table-cell !important;*/
/*   }*/

.div-app{
  height: 100%;
}


/*美化滚动条*/
html::-webkit-scrollbar {
  width: 0;
  height: 0;
}
html::-webkit-scrollbar-track {
  background-color:#F5F5F5;
}
html::-webkit-scrollbar-thumb {
  background-color:#c8c8c8;
}
</style>
