package com.example.toyproject.controller.search;

import com.example.toyproject.dto.BoardSearchDto;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

@Controller
@Slf4j
@RequestMapping("/board/movie-search")
public class MovieController {

    //tmdb에서 발급받은 api key
    //네이버는 서비스 종료
    private final String apiKey = "abcfd6b3d2946500dfadcbe57d6d5014";

    @GetMapping
    public String home(Model model) {
        model.addAttribute("keyword", null);
        return "/form/board/movie-search";
    }

    @PostMapping
    public String search(@ModelAttribute("keyword") String keyword, Model model) {
        try {
            String encodedKeyword = null;
            try {
                encodedKeyword = URLEncoder.encode(keyword, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException("encoding error", e);
            }
            String apiURL = "https://api.themoviedb.org/3/search/movie?api_key="
                    + apiKey + "&query="+keyword;
            //log.info("apiURL={}",apiURL);
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> response = restTemplate.getForEntity(apiURL, String.class);

            JSONObject jsonObject = new JSONObject(response.getBody());
            JSONArray results = jsonObject.getJSONArray("results");
            List<BoardSearchDto> boardSearchDtoList = new ArrayList<>();
            // need: jsonarray길이가 0일때 errormessage 호출 제작 필요

            for (int i = 0; i < results.length(); i++) {
                JSONObject obj = results.getJSONObject(i);
                //각 영화의 페이지 정보 가져오기
                //영화 디렉터를 가져오려면 각 영화의 페이지로 이동 후 파싱해야함.
                int movieId = results.getJSONObject(i).getInt("id");
                String eachUrl = "https://api.themoviedb.org/3/movie/"
                        + movieId + "/credits?api_key=" + apiKey;
                ResponseEntity<String> responseEach = restTemplate.getForEntity(eachUrl, String.class);
                JSONObject jsonObjectEach = new JSONObject(responseEach.getBody());
                JSONArray crew = jsonObjectEach.getJSONArray("crew");
                //dto set
                String title = null;
                String director = null;
                String image = null;
                for (int j = 0; j < crew.length(); j++) {
                    JSONObject crewMember = crew.getJSONObject(j);
                    if (crewMember.getString("job").equals("Director")) {
                        director = crewMember.getString("name");
                    }
                }
                title = obj.getString("title");
                //String director = obj.getString("Director");
                image = obj.getString("poster_path");

                boardSearchDtoList.add(BoardSearchDto.builder()
                        .title(title)
                        .creator(director)
                        .imageURL("https://image.tmdb.org/t/p/w500/"+image).build());

            }
            model.addAttribute("boardSearchDtoList", boardSearchDtoList);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return "/form/board/movie-search";
    }
}
