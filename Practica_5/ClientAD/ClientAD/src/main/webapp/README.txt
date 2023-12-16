Instruccions per mostrar les imatges del servidor Rest accedint a la carpeta que les té guardades:

    **Al jsp o arxiu que mostra imatges afegir:**

        <%@ page import="Aux.ConnectionUtil"%> <!-- d'aquesta manera importem el canvi d'adreça web de localhost a IP -->
    
        String addr = ConnectionUtil.getServerAddr();


    **Allà on es vulgui mostrar una imatge escriure:**

        "http://"+ addr +"/RestAD/images" + (filename imatge)

        (generalment es veurà tal que així)
        (blabla) img src='http://"+ addr + "/RestAD/images/" + i.getFilename()+"' (blabla)


    **Allà on es vulgui clickar i descarregar una imatge escriure:**

        "http://"+ addr +"/RestAD/resources/jakartaee9/getImage/" + (filename imatge)
