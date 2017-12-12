
(function() {
	var UserManager = Base.extend({
		
		//事件注册
		initEvent : function() {
			var me = this;
	
			$("#add_btn").click(function() {
				me.doQuery();
			});
	
		},
		
		init : function() {
			var me = this;
			me.initEvent();
			me.doQuery();
		},
		
		//查询
		doQuery : function(pageNumber, pageSize, params){
			var me = this;
			if (!params)  params = new Object();
			params.pageNumber = pageNumber || 1 ;
			params.pageSize = pageSize || 10;
			
			var search_val = $("#search_input").val();
			if (!WYUtil.isEmpty(search_val)) {
				params.USER_NAME = search_val;
			}
			
			Invoker.async("UserController", "queryUserInfo", params, function(data){
				var pageCount = 1;
				if(data && data.result){
					me.initTableData(data.result);
					pageCount = data.result.pageCount;
				}
				
				laypage({
					cont: $('#navPage'),//容器。值支持id名、原生dom对象，jquery对象。【如该容器为】：<div id="page1"></div>
					pages: pageCount, //总页数
					curr: params.pageNumber, //当前页
					groups: '5', //连续显示分页数
					skip: true, //是否开启跳页
					jump: function(obj, first){ //触发分页后的回调
						if(!first){ //点击跳页触发函数自身，并传递当前页：obj.curr
							page_curr = obj.curr;
							me.doQuery(obj.curr);
						}
					}
				});
			});
		},
		
		initTableData : function(data){
			var me = this;
			$("#data_list").find("tr:not(:first)").remove();
			var total = data.total;
			if(total > 0){
				for(var i=0; i<data.rows.length; i++){
					var item = data.rows[i];
					var curr = $("#tr_template").clone().removeAttr("id").show();
					curr.data("data", item);
					WYUtil.setInputDomain(item, curr);
	
			        $("#data_list").append(curr);
			        
					$("#delete_detail",curr).click(function() {
						var ele = $(this).closest("tr");
			        	var data = ele.data("data");
			        	Utils.confirm('是否确定停用？', {yes:function(){
			        		var me = this;
			    			var params = {};
			    			params.user_id = user_id;
			    			Invoker.async("UserController", "disableUser", params, function(data){
			    				if(data.res_code == '00000'){
			    					Utils.alert("停用成功！");
			    					me.doQuery();
			    				}else{
			    					Utils.alert("停用失败！");
			    				}			
			    			});
						}});
					});
				}
			}else{
				var error_tr = '<tr><td colspan="99" align="center"><font color="red">暂无数据</font></td></tr>';
				$("#data_list").append(error_tr);
			}
		}
		
	});
	
	window.UserManager = new UserManager();
}());

$(function() {
	UserManager.init();
});