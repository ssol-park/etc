package com.study.etc.hash;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public class ConsistentHashUtil {
    private final TreeMap<Integer, String> ring = new TreeMap<>();
    private final Map<String, List<Integer>> serverData = new HashMap<>(); // 서버별 데이터 저장소
    private final int numberOfReplicas;

    public ConsistentHashUtil(int numberOfReplicas) {
        this.numberOfReplicas = numberOfReplicas;
    }

    // 서버 추가
    public void addServer(String server) {
        for (int i = 0; i < numberOfReplicas; i++) {
            int hash = hash(server + i);
            ring.put(hash, server);
        }
        serverData.put(server, new ArrayList<>()); // 데이터 저장소 생성
    }

    // 서버 제거 (데이터 이동 포함)
    public void removeServer(String server) {
        if (!serverData.containsKey(server)) return;

        // 서버의 기존 데이터를 새로운 서버로 이동
        List<Integer> migratingData = serverData.get(server);
        for (Integer key : migratingData) {
            String newServer = getServer(String.valueOf(key)); // 새로운 서버 찾기
            if (newServer != null) {
                serverData.get(newServer).add(key); // 새로운 서버로 데이터 이동
            }
        }

        // 원형 해시에서 해당 서버 제거
        for (int i = 0; i < numberOfReplicas; i++) {
            int hash = hash(server + i);
            ring.remove(hash);
        }

        // 원래 서버 데이터 삭제
        serverData.remove(server);
    }

    // 키를 저장 (어떤 서버에 저장할지 결정)
    public void put(int key) {
        String server = getServer(String.valueOf(key));
        if (server != null) {
            serverData.get(server).add(key);
        }
    }

    // 키 조회
    public String getServer(String key) {
        if (ring.isEmpty()) return null;

        int hash = hash(key);
        SortedMap<Integer, String> tailMap = ring.tailMap(hash);
        int targetHash = tailMap.isEmpty() ? ring.firstKey() : tailMap.firstKey();
        return ring.get(targetHash);
    }

    // 해시 함수 (SHA-256 사용)
    private int hash(String key) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] bytes = md.digest(key.getBytes(StandardCharsets.UTF_8));
            return ((bytes[0] & 0xFF) << 24) | ((bytes[1] & 0xFF) << 16) | ((bytes[2] & 0xFF) << 8) | (bytes[3] & 0xFF);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 not available");
        }
    }

}
