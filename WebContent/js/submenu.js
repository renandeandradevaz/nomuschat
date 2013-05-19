
id = 0;

function gerarLinkCompleto(link){
	document.location.href= link + "/" + id; 
}

jQuery(document).ready(function( $ ){
	
	/* Seleciona todos os submenus da tela e os esconde */
	var subMenus = $('.dropdown-menu');
	subMenus.hide();

	/* Esconde todos os submenus quando é clicado qualquer elemento que não seja
	 * um elemento que deva mostrar um submenu. 
	*/
	$('body').click(function(){
		subMenus.hide();
	});
	
	$(function(){
	    $('tr').click(function(e){
	    	
	    	/* Pego o valor do id do tr clicado */
	        id = $(this).attr('id');
	        e.stopPropagation();
	        subMenus.hide();
	        
	       if(id != null){

	    	   /* Pego o id da entidade e também o id do submenu */
	    	   var posicaoUnderline = id.indexOf("_");
	    	   var idSubMenu = '#' + id.substring(0, posicaoUnderline);
	    	   id = id.substring(posicaoUnderline + 1);
	    	   
	    	   /* Atribuo propriedades css ao submenu para aparecer ao lado do ponteiro do mouse */
	    	   $(idSubMenu).css("left", e.pageX);
	    	   $(idSubMenu).css("top", e.pageY - 55);
	    	   $(idSubMenu).css("display", "block");
	    	   
	    	   /* Mostro o submenu */
	    	   $(idSubMenu).show();
	    	   
	    	   /* Se o submenu não tiver nenhum li visível, eu o escondo */
	    	   if ($(idSubMenu + ' li:visible').length == 0)
	    	   {
	    		   $(idSubMenu).hide();
	    	   }
	       }
	    });
	});	
});