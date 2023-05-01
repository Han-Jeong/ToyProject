package com.example.toyproject.controller.search;

import com.example.toyproject.dto.BoardSearchDto;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;


@Controller
@Slf4j
@RequestMapping("/board/book-search")
public class BookController {

    // 네이버 도서 검색 api key
    private final String CLIENT_ID = "22MWQam8qBycNFDUAGCp";
    private final String CLIENT_SECRET = "r84H3YH0Ba";

    @GetMapping
    public String home(Model model) {
        String keyword = "";
        model.addAttribute("keyword", keyword);
        return "form/board/book-search";
    }

    @PostMapping
    public String search(@ModelAttribute("keyword") String keyword, Model model) {
        try {
            String encodedKeyword = URLEncoder.encode(keyword, "UTF-8");
            String apiURL = "https://openapi.naver.com/v1/search/book.json?query=" + encodedKeyword;
            URL url = new URL(apiURL);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("X-Naver-Client-Id", CLIENT_ID);
            con.setRequestProperty("X-Naver-Client-Secret", CLIENT_SECRET);
            int responseCode = con.getResponseCode();
            BufferedReader br;
            if (responseCode == 200) { // 정상 호출
                br = new BufferedReader(new InputStreamReader(con.getInputStream()));
            } else {  // 에러 발생
                br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
            }
            String inputLine;
            StringBuffer response = new StringBuffer();
            while ((inputLine = br.readLine()) != null) {
                response.append(inputLine);
            }
            br.close();


            JSONObject jsonObject = new JSONObject(response.toString());
            JSONArray jsonArray = (JSONArray) jsonObject.get("items");
            List<BoardSearchDto> boardSearchDtoList = new ArrayList<>();

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);
                String title = obj.getString("title");
                String author = obj.getString("author");
                String image = obj.getString("image");

                boardSearchDtoList.add(BoardSearchDto.builder()
                        .title(title)
                        .creator(author)
                        .imageURL(image).build());

            }
            model.addAttribute("boardSearchDtoList", boardSearchDtoList);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return "form/board/book-search";
    }
}
