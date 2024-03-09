package pl.zzpj.hellokitty;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.intellij.ide.BrowserUtil;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.jetbrains.annotations.NotNull;

public class Prompt extends AnAction {

    private String catPhotoURL;

    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.accept("application/json");
        requestSpecification.header("x-api-key", Constants.API_KEY);

        Response response = requestSpecification.get(Constants.BASE_URL);

        Gson gson = new Gson();
        JsonArray jsonArray = gson.fromJson(response.getBody().asString(), JsonArray.class);
        JsonObject jsonObject = jsonArray.get(0).getAsJsonObject();
        String imageURL = jsonObject.get("url").getAsString();
        BrowserUtil.browse(imageURL);
    }
}
