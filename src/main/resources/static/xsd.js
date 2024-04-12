class XSD {
  url;
  doc;

  constructor(url,xsd) {
    this.doc = xsd;
    this.url = url;
  }
  
  static async load(url) {
    const response = await fetch(url)
    if( !response.ok )throw new Error("xsd document not loaded")

    const text = await response.text()

    if (window.DOMParser)
    {
      const parser = new DOMParser()
      const xmlDoc = parser.parseFromString(text, "text/xml")
      return new XSD( new URL(url, xmlDoc.baseURI).href ,xmlDoc)
    }
    else // Internet Explorer
    {
      const xmlDoc = new ActiveXObject("Microsoft.XMLDOM")
      xmlDoc.async = false
      xmlDoc.loadXML(text)
      return new XSD( new URL(url, xmlDoc.baseURI).href ,xmlDoc)
    }
  }

  get includes() {
    return Array.from(this.doc.querySelectorAll('include'))
      .filter( e => e.namespaceURI == 'http://www.w3.org/2001/XMLSchema' )
      .map( e => new XSDInclude(this, e) )
  }

  get imports() {
    return Array.from(this.doc.querySelectorAll('import'))
      .filter( e => e.namespaceURI == 'http://www.w3.org/2001/XMLSchema' )
      .map( e => new XSDImport(this,e) )
  }
}

class XSDInclude {
  elem;
  xsd;
  constructor(xsd,elem) {
    this.xsd = xsd;
    this.elem = elem;
  }

  get url(){
    return new URL(this.elem.attributes.schemaLocation.value, this.xsd.url).href
  }
}

class XSDImport {
  elem;
  xsd;
  constructor(xsd,elem) {
    this.xsd = xsd;
    this.elem = elem;
  }

  get url(){
    return new URL(this.elem.attributes.schemaLocation.value, this.xsd.url).href
  }
}

