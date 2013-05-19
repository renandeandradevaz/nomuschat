<%@ include file="/base.jsp" %> 

<form id="formNomusChat" method="post" action="http://localhost:8080/nomuschat/login/loginVindoDoPCP" target="novaAbaNomusChat" style="display: none" >
	<input type="hidden" id='loginNomusChat' name="loginNomusChat" value="renan" />
	<input type="hidden" id='senhaNomusChat' name="senhaNomusChat" value="1234" />
	<input type="hidden" id="nomeNomusChat" name="nomeNomusChat" value="Renan Vaz" />
	<input type="hidden" id="nomeEmpresaNomusChat" name="nomeEmpresaNomusChat" value="Projetec" />
	<input type="hidden" id="enderecoNomusChat" name="enderecoNomusChat" value="http://localhost:8080/nomuschat" />
</form>

<div id='usuariosLogados'>
	<h5> Usuários online </h5>  
	<ul> </ul>	
</div>

<script type="text/javascript">

	jQuery(document).ready(function(){
    	
		if(navigator.appName != 'Microsoft Internet Explorer'){
    	
			loginNomusChat = jQuery("#loginNomusChat").val();
    		senhaNomusChat = jQuery("#senhaNomusChat").val();
    		nomeNomusChat = jQuery("#nomeNomusChat").val();
    		nomeEmpresaNomusChat = jQuery("#nomeEmpresaNomusChat").val();
    		enderecoNomusChat = jQuery("#enderecoNomusChat").val();
		    
		    var broadcastMessageCallback = function(nomeDestinatario, codigoDestinatario, msg) {
		        
		    jQuery("#chat"+codigoDestinatario).chatbox("option", "boxManager").addMsg(nomeDestinatario, msg);
		
				jQuery.ajax({ 
			        type: 'GET',
			        url: enderecoNomusChat + "/chat/recebeMensagem?loginNomusChat=" + loginNomusChat + "&senhaNomusChat="+ senhaNomusChat + "&nomeNomusChat="+ nomeNomusChat + "&nomeEmpresaNomusChat="+ nomeEmpresaNomusChat + "&destinatario=" + codigoDestinatario + "&mensagem="+ msg,
			        dataType: 'jsonp', 
			        jsonp: false,
					jsonpCallback: "jsonMensagemEnviada",
			        success: function(data) { 
			        }
		    	});
		    }
	
			chatboxManager.init({messageSent : broadcastMessageCallback});
	
			exibirUsuariosLogados();
	
			intervaloVerificacaoExistenciaNovasMensagens = setInterval(verificaExistenciaNovasMensagens, 1000);
			setInterval(exibirUsuariosLogados, 10000);

			jQuery(document).on('click', '#usuariosLogados li', function(){  
				
				var keyRemetente = jQuery(this).attr("id");
				var nomeRemetente = jQuery(this).text();
				
				chatboxManager.addBox("chat"+keyRemetente, {dest: keyRemetente, title: '' , first_name: nomeRemetente, last_name: '' });
			});
    	}
    	
    	else{
    		
    		alert("Chat ainda não suportado no internet explorer. Por favor, utilize outro navegador.");
    	}
 
	});
    
    function exibirUsuariosLogados(){
		
	    jQuery.ajax({ 
	        type: 'GET',
	        url: enderecoNomusChat + "/chat/exibirUsuariosLogados?loginNomusChat=" + loginNomusChat + "&senhaNomusChat="+ senhaNomusChat + "&nomeNomusChat="+ nomeNomusChat + "&nomeEmpresaNomusChat="+ nomeEmpresaNomusChat,
	        dataType: 'jsonp', 
	        jsonp: false,
		    jsonpCallback: "jsonUsuariosLogados",
	        success: function(data) { 
	        	
	        	jQuery("#usuariosLogados ul").empty();
	        	
	            jQuery.each(data.list, function(i, item){

	               jQuery("#usuariosLogados ul").append("<li>");
	               jQuery("#usuariosLogados li:last").attr("id", item.empresa.nome + "_" + item.login);
	               jQuery("#usuariosLogados li:last").append(item.nome);

	 	    	});
	        }
	    });	    
	}
    
    function verificaExistenciaNovasMensagens(){
		
	    jQuery.ajax({ 
	        type: 'GET',
	        url: enderecoNomusChat + "/chat/verificaExistenciaNovasMensagens?loginNomusChat=" + loginNomusChat + "&senhaNomusChat="+ senhaNomusChat + "&nomeNomusChat="+ nomeNomusChat + "&nomeEmpresaNomusChat="+ nomeEmpresaNomusChat,
	        dataType: 'jsonp', 
	        jsonp: false,
			jsonpCallback: "jsonVerificacaoExistenciaNovasMensagens",
	        success: function(data) { 
	            jQuery.each(data.list, function(i, item){

	               chatboxManager.addBox("chat"+item.remetente, {dest: item.remetente, title: '' , first_name: item.nome, last_name: '' });
	               jQuery("#chat"+item.remetente).chatbox("option", "boxManager").addMsg(item.nome, item.mensagem);

	 	    	});
	        }
	    });
	    
	    var existePeloMenosUmChatAberto = false;
	    
	    jQuery(".ui-chatbox").each(function(){
	    	
	    	if(jQuery(this).css("display") == "block"){
	    		
	    		existePeloMenosUmChatAberto = true;
	    		return;
	    	}	    	
	    });
	    
	    clearInterval(intervaloVerificacaoExistenciaNovasMensagens);
	    
	    if(existePeloMenosUmChatAberto){
	    	
		    intervaloVerificacaoExistenciaNovasMensagens = setInterval(verificaExistenciaNovasMensagens, 1000);
	    }
	    else{
	    	
	    	intervaloVerificacaoExistenciaNovasMensagens = setInterval(verificaExistenciaNovasMensagens, 10000);
	    }	    
	} 
</script>