<%@ include file="/base.jsp" %> 

<input type="hidden" id='loginNomusChat' name="loginNomusChat" value="${sessaoUsuario.usuario.login}" />
<input type="hidden" id='senhaNomusChat' name="senhaNomusChat" value="${sessaoUsuario.usuario.senha}" />
<input type="hidden" id="nomeNomusChat" name="nomeNomusChat" value="${sessaoUsuario.usuario.nome}" />
<input type="hidden" id="nomeEmpresaNomusChat" name="nomeEmpresaNomusChat" value="${sessaoUsuario.usuario.empresa.nome}" />
<input type="hidden" id="enderecoNomusChat" name="enderecoNomusChat" value="${enderecochat}" />

<div id='usuariosLogados'>
	<h5> Usuários online </h5>  
	<ul> </ul>	
</div>

<script type="text/javascript">

	window_focus = true;
	intervaloPiscarMensagem = setInterval(piscarMensagem, 3000);
	remetenteGlobal = '';

	jQuery(document).ready(function(){
		
		jQuery(window).focus(function() {
		    window_focus = true;
		    jQuery("title").text("Nomus Chat");
		    remetenteGlobal = '';
		}).blur(function() {
		    window_focus = false;
		    remetenteGlobal = '';
		});
    	
		if(navigator.appName != 'Microsoft Internet Explorer'){
    	
			loginNomusChat = jQuery("#loginNomusChat").val();
    		senhaNomusChat = jQuery("#senhaNomusChat").val();
    		nomeNomusChat = jQuery("#nomeNomusChat").val();
    		nomeEmpresaNomusChat = jQuery("#nomeEmpresaNomusChat").val();
    		enderecoNomusChat = jQuery("#enderecoNomusChat").val();
		    
		    var broadcastMessageCallback = function(nomeDestinatario, codigoDestinatario, msg) {
		    	
			    jQuery("#chat"+codigoDestinatario.replace(".", "\\.")).chatbox("option", "boxManager").addMsg(nomeNomusChat, msg);
			    
			    msg = substituirCaracteresEspeciaisAntesDeEnviarMensagem(msg);
			
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
	
			intervaloVerificacaoExistenciaNovasMensagens = setInterval(verificaExistenciaNovasMensagens, 5000);
			setInterval(exibirUsuariosLogados, 300000);

			jQuery(document).on('click', '#usuariosLogados .usuario', function(){  
				
				var keyRemetente = jQuery(this).attr("id");
				var nomeRemetente = jQuery(this).text();
				
				chatboxManager.addBox("chat"+keyRemetente, {dest: keyRemetente, title: '' , first_name: nomeRemetente, last_name: '' });
				jQuery(".ui-chatbox-input-box:last").focus();
			});
    	}
    	
    	else{
    		
    		alert("Chat ainda não suportado no internet explorer. Por favor, utilize outro navegador.");
    	}
 
	});
	
	function substituirCaracteresEspeciaisAntesDeEnviarMensagem(string){
		
		while (string.indexOf("#") != -1) {
			
	 		string = string.replace("#", "!!!tralha!!!");
		}
		
		while (string.indexOf("%") != -1) {
			
	 		string = string.replace("%", "!!!porcentagem!!!");
		}
		
		while (string.indexOf("+") != -1) {
			
	 		string = string.replace("+", "!!!mais!!!");
		}
		
		return string;
	}
    
    function exibirUsuariosLogados(){
		
	    jQuery.ajax({ 
	        type: 'GET',
	        url: enderecoNomusChat + "/chat/exibirUsuariosLogados?loginNomusChat=" + loginNomusChat + "&senhaNomusChat="+ senhaNomusChat + "&nomeNomusChat="+ nomeNomusChat + "&nomeEmpresaNomusChat="+ nomeEmpresaNomusChat,
	        dataType: 'jsonp', 
	        jsonp: false,
		    jsonpCallback: "jsonUsuariosLogados",
	        success: function(data) { 
	        	
	        	jQuery("#usuariosLogados ul").empty();

	        	var empresas = {};
	        	var nomesUsuarios = {};

	        	jQuery.each(data.list, function(i, item){

	            	if(empresas[item.empresa.nome] === undefined){

		        		empresas[item.empresa.nome] = new Array();
	            	}

	        		empresas[item.empresa.nome].push(item.login);
	        		nomesUsuarios[item.keyEmpresaUsuario] = item.nome;
	 	    	});
	        	
	        	for (var keyEmpresa in empresas) {
	        		
	        		jQuery("#usuariosLogados ul").append("<li>");
	        		jQuery("#usuariosLogados li:last").addClass("empresa");
		            jQuery("#usuariosLogados li:last").text(keyEmpresa);
		            jQuery("#usuariosLogados li:last").append("<ul>");

	        		for (var keyUsuario in empresas[keyEmpresa]) {

	        			jQuery("#usuariosLogados ul:last").append("<li>");
	            		jQuery("#usuariosLogados li:last").addClass("usuario");
	            		jQuery("#usuariosLogados li:last").text(nomesUsuarios[keyEmpresa + "_" + empresas[keyEmpresa][keyUsuario]]);
	            		jQuery("#usuariosLogados li:last").attr("id", keyEmpresa + "_" + empresas[keyEmpresa][keyUsuario]);
	        		}
	        	}
	        }
	    });	    
	}
    
    function piscarMensagem(){
    	
    	if(window_focus || remetenteGlobal == ''){
    		
    		jQuery("title").text("Nomus Chat");    		
    	}
    	
    	else{
    		
	    	if(jQuery("title").text() == 'Nomus Chat'){
	    		
	    		jQuery("title").text(remetenteGlobal + " diz:");
	    	}
	    	else{
	    		
	    		jQuery("title").text("Nomus Chat");
	    	}
    	}
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
	               jQuery("#chat"+item.remetente.replace(".", "\\.")).chatbox("option", "boxManager").addMsg(item.nome, item.mensagem);
	               
	               remetenteGlobal = item.nome;
	               
	               clearInterval(intervaloPiscarMensagem);
	               piscarMensagem();
	               intervaloPiscarMensagem = setInterval(piscarMensagem, 3000);
	               
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
	    	
		    intervaloVerificacaoExistenciaNovasMensagens = setInterval(verificaExistenciaNovasMensagens, 10000);
	    }
	    else{
	    	
	    	intervaloVerificacaoExistenciaNovasMensagens = setInterval(verificaExistenciaNovasMensagens, 30000);
	    }	    
	} 
</script>