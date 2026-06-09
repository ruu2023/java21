package com.example.demo.service;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class DemoService {
    public List<String> hello(List<String> input) {
        List<String> all = Arrays.asList("Z", "Y", "A", "B", "D", "E", "F");
        List<Dto> list = getDto();
        List<Dto> devList = list.stream()
                .filter(x -> x.getCode().startsWith("DEV")).collect(Collectors.toList());
        Set<String> devResults = devList.stream()
                .map(Dto::getResult).collect(Collectors.toSet());
        List<Dto> otherList = list.stream()
                .filter(x -> !x.getCode().startsWith("DEV")).collect(Collectors.toList());

        // DEV を含んでいるか
        Set<String> devCandidate = devList.stream()
                .filter(x -> input.contains(x.getCode()))
                .map(Dto::getResult).collect(Collectors.toSet());

        // OHTER を含んでいるか
        Set<String> otherCandidate = otherList.stream()
                .filter(x -> input.contains((x.getCode())))
                .map(Dto::getResult).collect(Collectors.toSet());

        List<String> result = new ArrayList<>();

        // case 両方含む
        if(devCandidate.size() > 0 && otherCandidate.size() > 0) {
            result = devCandidate.stream()
                    .filter(otherCandidate::contains).collect(Collectors.toList());
            if(result.size() == 0) {
                result = devCandidate.stream().toList();
            }
        } else if(devCandidate.size() > 0) {
        // case DEV のみ
            result = devCandidate.stream().toList();
        } else if(otherCandidate.size() > 0) {
        // ここから DEV を含まない
        // case OTHER のみ
            result = otherCandidate.stream()
                    .filter(x -> !devResults.contains(x)).collect(Collectors.toList());
        } else (result.size() == 0) {
        // case 両方含んでいない
            result = all;
        }
        return result;
    }

    private List<Dto> getDto() {
        List<Dto> list = new ArrayList<>();
        list.add(new Dto("Z", "DEV1"));
        list.add(new Dto("Z", "DEV2"));
        list.add(new Dto("Z", "LOW1"));
        list.add(new Dto("Z", "LOW2"));
        list.add(new Dto("Y", "DEV3"));
        list.add(new Dto("Y", "DEV2"));
        list.add(new Dto("Y", "LOW3"));
        list.add(new Dto("B", "DEV2"));
        list.add(new Dto("A", "DEV3"));
        list.add(new Dto("A", "DEV2"));
        list.add(new Dto("A", "LOW3"));
        list.add(new Dto("D", "LOW1"));
        list.add(new Dto("E", "LOW3"));
        list.add(new Dto("F", "LOW1"));
        return list;
    }

    @Builder
    @Getter
    static class Dto {
        String result;
        String code;

        public Dto(String result, String code) {
            this.result = result;
            this.code = code;
        }
    }
}
