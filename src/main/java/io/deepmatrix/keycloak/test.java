package io.deepmatrix.keycloak;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.quarkus.runtime.QuarkusApplication;
import io.quarkus.runtime.annotations.QuarkusMain;

@QuarkusMain
public class test implements QuarkusApplication { 
  final static String GEO_SERVER_URL = "http://localhost:8600/geoserver/rest/workspaces";
  @Override
  public int run(String... args) throws Exception {
    System.out.println("...THIS IS ONLY THE BEGINING...");
    // Keycloak kc = KeycloakBuilder.builder()
    //   .serverUrl("https://kc.deepmatrix.io:8443/")
    //   .realm("master")
    //   .clientId("quarkus-admin-client")
    //   .clientSecret("GXyLdvdHB3wWSwUkiEPcL4CF0B9EmhFE")
    //   .username("dmadmin")
    //   .password("Admin@dm2022")
    //   .build();
    // RealmResource realmResource = kc.realm("master");
    // UsersResource usersResource = realmResource.users();
    // List<UserRepresentation> users = usersResource.list();
    // for (UserRepresentation user : users) {
    //   System.out.println(user.getEmail() + "::" + user.getRealmRoles());
    // }
      String workspace = "workspace1";
      String storeName = "store1";
      String layerName = "layer2";
      String filePath = "/Users/anilbhat/test-data/patniitop-orthophoto.tif";
    // call http client
    // createWorkSpace(workspace);
    uploadFile(filePath);
    // createStore(workspace,storeName);
    // createLayer(workspace, storeName, layerName);
    // System.out.println(usersResource.count());
    
    //usersResource.create(userRepresenta)
    return 1;
 }

  private void uploadFile(String filePath) throws io.tus.java.client.ProtocolException, IOException {
    UploadFile fileUpload = new UploadFile(filePath);
    fileUpload.uploadFile();
  }

  private void createWorkSpace(String workspace) throws IOException {
    String xmlRequest = String.format("<workspace><name>%s</name></workspace>", workspace);
    doPost("", xmlRequest);
  }

  private void doPost(String path, String xmlRequest) throws IOException {
    
    URL url = new URL(GEO_SERVER_URL +  path);

    // Open a connection(?) on the URL(??) and cast the response(???)
    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    connection.setRequestMethod("POST");
    connection.setDoOutput(true);

    // Now it's "open", we can set the request method, headers etc.
    connection.setRequestProperty("accept", "application/xml");
    connection.setRequestProperty("Content-Type", "application/xml");

    String auth = "admin" + ":" + "geoserver";
//     Base64 base64 = new Base64.getEncoder().;
    byte[] encodedAuth = Base64.getEncoder().encode(auth.getBytes(StandardCharsets.UTF_8));

    String authHeaderValue = "Basic " + new String(encodedAuth);
    connection.setRequestProperty("Authorization", authHeaderValue);
    
    OutputStream outputStream = connection.getOutputStream();
		byte[] b = xmlRequest.getBytes("UTF-8");
		outputStream.write(b);
		outputStream.flush();

    // This line makes the request
    InputStream inputStream = connection.getInputStream();
		byte[] res = new byte[2048];
		int i = 0;
		StringBuilder response = new StringBuilder();
		while ((i = inputStream.read(res)) != -1) {
			response.append(new String(res, 0, i));
		}
		inputStream.close();

		System.out.println("Response= " + response.toString());

  }


  // curl -v -u admin:geoserver -XPOST -H "Content-type: application/xml" -d "<coverageStore><name>sample</name><workspace>patni</workspace><enabled>true</enabled><type>GeoTIFF</type><url>file:///geoserver/patniitop-orthophoto.tif</url></coverageStore>" http://localhost:8600/geoserver/rest/workspaces/patni/coveragestores
  private void createStore(String workspace, String storeName) throws IOException {
    String path = String.format("/%s/coveragestores", workspace);
    String xmlRequest = String.format("<coverageStore><name>%s</name><workspace>%s</workspace><enabled>true</enabled><type>GeoTIFF</type><url>%s</url></coverageStore>", storeName, workspace, "file:///geoserver/patniitop-orthophoto.tif");
    doPost(path, xmlRequest);
    return;
  }

  // curl -v -u admin:geoserver -XPOST -H "Content-type: application/xml" -d "<coverage><name>my_layer</name><title>my_layer</title><srs>EPSG:4326</srs></coverage>" http://localhost:8600/geoserver/rest/workspaces/patni/coveragestores/patni/coverages
  public boolean createLayer(String workspace, String storeName, String layerName) throws IOException{
    String path = String.format("/%s/coveragestores/%s/coverages", workspace, storeName);
    String xmlRequest = String.format("<coverage><name>%s</name><title>%s</title><srs>EPSG:4326</srs></coverage>", layerName, layerName);
    doPost(path, xmlRequest);
    return true;
  }
}