<%@ include file="/base.jsp" %> 


<h3 style="text-align: center; margin-top: 40px;">  Bem vindo(a) ${sessaoUsuario.usuario.nome}! </h3>


<script>

	setTimeout(redirecionarParaChat, 3000);
	
	function redirecionarParaChat(){
		
		window.location.href = '<c:url value="/chat/chat"/>';
	}


</script>

