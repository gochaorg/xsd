package xyz.cofe;

import org.teavm.jso.browser.Window;
import org.teavm.jso.dom.html.HTMLButtonElement;
import org.teavm.jso.dom.html.HTMLDocument;
import org.teavm.jso.dom.html.HTMLElement;
import org.teavm.jso.dom.html.HTMLInputElement;
import org.teavm.jso.ajax.XMLHttpRequest;
import org.teavm.jso.dom.xml.DOMParser;

public class Client {
    public static void main(String[] args) {
        new Client().buildUI();
    }

    private void buildUI() {
        var div = HTMLDocument.current().createElement("div");
        HTMLDocument.current().getBody().appendChild(div);

        HTMLInputElement urlAddr = HTMLDocument.current().createElement("input").cast();
        urlAddr.setType("text");
        div.appendChild(urlAddr);

        HTMLButtonElement but = HTMLDocument.current().createElement("button").cast();
        but.setInnerText("get");
        div.appendChild(but);

        HTMLElement res = HTMLDocument.current().createElement("div");
        div.appendChild(res);
        but.addEventListener("click", evt -> {
            getPlainText(urlAddr.getValue(), res);
        });
    }

    private void getPlainText(String addr, HTMLElement result) {
        var xhr = XMLHttpRequest.create();
        xhr.onComplete(() -> {
            xhr.getResponseXML();
            result.setInnerText(xhr.getResponseText());

            System.out.println("bla bla");
        });
        xhr.onError(err->{

        });
        xhr.open("GET", addr);
        xhr.send();
    }
}
