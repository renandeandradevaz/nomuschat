package scada.listener;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import scada.interceptor.InterceptadorDeAutorizacao;
import scada.sessao.SessaoOperador;
import scada.util.Util;

public final class CounterListener implements HttpSessionListener {

	private static int count = 1;
	private ServletContext context = null;

	public synchronized void sessionCreated(HttpSessionEvent se) {
		count++;
		log("sessionCreated(" + se.getSession().getId() + ") count=" + count);
		se.getSession().setAttribute("count", new Integer(count));
	}

	public synchronized void sessionDestroyed(HttpSessionEvent se) {

		if (Util.preenchido(se.getSession().getAttribute("sessaoOperador")) && se.getSession().getAttribute("sessaoOperador") instanceof SessaoOperador) {

			SessaoOperador sessaoOperador = (SessaoOperador) se.getSession().getAttribute("sessaoOperador");

			if (Util.preenchido(InterceptadorDeAutorizacao.getUsuariosLogados()) && Util.preenchido(sessaoOperador) && Util.preenchido(sessaoOperador.getOperador()) && Util.preenchido(sessaoOperador.getOperador().getLogin())) {

				InterceptadorDeAutorizacao.getUsuariosLogados().remove(sessaoOperador.getOperador().getLogin());
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
		if (context != null)
			context.log("SessionListener: " + message);
		else
			System.out.println("SessionListener: " + message);
	}

}