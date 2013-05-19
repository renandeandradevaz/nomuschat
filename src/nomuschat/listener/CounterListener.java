package nomuschat.listener;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import nomuschat.interceptor.InterceptadorDeAutorizacao;
import nomuschat.sessao.SessaoUsuario;
import nomuschat.util.Util;

public final class CounterListener implements HttpSessionListener {

	private static int count = 1;
	private ServletContext context = null;

	public synchronized void sessionCreated(HttpSessionEvent se) {
		count++;
		log("sessionCreated(" + se.getSession().getId() + ") count=" + count);
		se.getSession().setAttribute("count", new Integer(count));
	}

	public synchronized void sessionDestroyed(HttpSessionEvent se) {

		if (Util.preenchido(se.getSession().getAttribute("sessaoUsuario")) && se.getSession().getAttribute("sessaoUsuario") instanceof SessaoUsuario) {

			SessaoUsuario sessaoUsuario = (SessaoUsuario) se.getSession().getAttribute("sessaoUsuario");

			if (Util.preenchido(InterceptadorDeAutorizacao.getUsuariosLogados()) && Util.preenchido(sessaoUsuario) && Util.preenchido(sessaoUsuario.getUsuario()) && Util.preenchido(sessaoUsuario.getUsuario().getLogin())) {

				InterceptadorDeAutorizacao.getUsuariosLogados().remove(sessaoUsuario.getUsuario().getEmpresa().getNome() + "_" + sessaoUsuario.getUsuario().getLogin());
			}
		}

		count--;
		log("sessionDestroyed(" + se.getSession().getId() + ") count=" + count);
		se.getSession().setAttribute("count", new Integer(count));
	}

	public static int getCount() {
		return count;
	}

	public void addCount() {
		count++;
	}

	private void log(String message) {
		if (context != null) {

			// context.log("SessionListener: " + message);
		}

		else {

			// System.out.println("SessionListener: " + message);
		}
	}

}