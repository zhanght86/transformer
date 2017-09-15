define(["jquery","jquery.ui.widget","transport","fileupload"],function($){
	function simpleupload(qp){
		var filemax = qp.filemax || 5 * 1024 * 1024;//文件大小的限制默认5M
		var filetype = qp.filetype || ["png","jepg","jpg","PNG","JPG","JEPG","doc","docx"];//上传文件后缀限制
		var $div = qp.$div;//上传的jquery对象一般为input
		var attachType = qp.attachType;//附件类型-区分是哪个业务上传的
		var hidFileId = qp.hidFileId || 'hidFileId';//存储返回的附件的Id
		var $progressbar = qp.progressbar;// 进度条
		if(!$div || !attachType){
			alert('上传配置信息错误');
			return;
		}
		// 上传文字
		var lab = $('<label>点击上传</label>')
		lab.css({
			cursor: 'pointer',
			color: '#fff',
			height: '20px',
			'line-height': '20px',
			padding: '3px 5px',
			display: 'inline-block',
			'background-color': '#398AC7',
			width: '80px',
			'border-radius':'5px',
			'text-align':'center'
		});

		// 上传文件对象
		var inupt_file = $('<input type="file" name="file" data-url="file/upload" multiple>');
		inupt_file.css({
			cursor: 'pointer',
			width:'78px',
			height:'20px',
			'line-height':'20px',
			'margin-left':'-78px',
			filter:'alpha(opacity=0)',
			opacity:'0',
			cursor: 'pointer'
		});
		var hid_file_id = $('<input type="hidden" name="'+hidFileId+'">');
		$div.append(lab).append(inupt_file).append(hid_file_id);
		//添加文件名提示
		var fileName ="";
		var $uploadinfo = $("<div class='uploadinfo'>");
		$uploadinfo.css({"color":"red","display":"inline"});
		$div.after($uploadinfo);
		inupt_file.fileupload({
			dataType: 'json',
			formData:{
				attachType : attachType
			},
			progressall:function (e, data) {//进度条
				if($progressbar){
					var progress = parseInt(data.loaded / data.total * 100, 10);
					$progressbar.progressbar('setValue', progress);
				}
			},
			add: function (e,data){//上传
				var file = data.files[0];
				var fileSize = file.size;
				fileName = file.name;
				var fileNameSplit = fileName.split(".");
				var fileEx = fileNameSplit[fileNameSplit.length - 1];
				for(var idx in filetype){
					if(filetype[idx] == fileEx){
						break;
					}
					if(idx == filetype.length - 1){
						$uploadinfo.html('文件类型必须为['+filetype.join(",")+']其中之一');
						return;
					}
				}
				//验证文件大小
				if(fileSize > filemax){
					$uploadinfo.html('文件大小不能超过['+(filemax/1024/1024)+']M');
					return;
				}
				$uploadinfo.html(fileName + " 上传中...");
				//上传
				data.submit();
			},
			//后台返回
			done: function (e, data) {
				if(data.result.success){
					$uploadinfo.html(fileName + " 上传成功");
					hid_file_id.val(data.result.fileId);
					if(qp.success){
						qp.success(data);
					}
				}else{
					$uploadinfo.html(fileName + " 上传失败");
					if(qp.success){
						qp.success(data);
					}
				}
			},
			fail: function(){
				$uploadinfo.html(fileName + " 上传失败");
				if(qp.success){
					qp.success(data);
				}
			}
		});
	}
	return {
		simpleupload:simpleupload
	};
});