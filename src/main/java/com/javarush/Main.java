package com.javarush;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.javarush.dao.CityDAO;
import com.javarush.dao.CountryDAO;
import com.javarush.domain.City;
import com.javarush.domain.Country;
import com.javarush.domain.CountryLanguage;
import com.javarush.redis.CityCountry;
import com.javarush.redis.Language;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisStringCommands;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;


import java.util.ArrayList;
import java.util.List;

import java.util.Set;
import java.util.stream.Collectors;

import io.lettuce.core.*;
import com.fasterxml.jackson.databind.*;

import static java.util.Objects.nonNull;


public class Main{

    private final SessionFactory sessionFactory;
    private final RedisClient redisClient;

    private final ObjectMapper mapper;

    private final CityDAO cityDAO;
    private final CountryDAO countryDAO;

    public Main(){
        sessionFactory = prepareRelationalDb();
        cityDAO = new CityDAO(sessionFactory);
        countryDAO = new CountryDAO(sessionFactory);

        redisClient = prepareRedisClient();
        mapper = new ObjectMapper();
    }

    private SessionFactory prepareRelationalDb(){
        Configuration configuration = new Configuration()
                .addAnnotatedClass(City.class)
                .addAnnotatedClass(Country.class)
                .addAnnotatedClass(CountryLanguage.class);
        return configuration.buildSessionFactory();
    }

    private void shutdown(){
        if(nonNull(sessionFactory)){
            sessionFactory.close();
        }
        if(nonNull(redisClient)){
            redisClient.shutdown();
        }
    }

    private List<City> fetchData(Main main) {
        try(Session session = main.sessionFactory.getCurrentSession()){
            List<City> allCities = new ArrayList<>();
            session.beginTransaction();
            List<Country> countries = main.countryDAO.getAll();
            int totalCount = main.cityDAO.getTotalCount();
            int step = 500;
            for (int i = 0; i < totalCount; i += step) {
                allCities.addAll(main.cityDAO.getItems(i,step));
            }
            session.getTransaction().commit();
            return allCities;
        }
    }

    private List<CityCountry> transformData(List<City> cities){
        return cities.stream().map(city->{
            CityCountry res = new CityCountry();
            res.setId(city.getId());
            res.setCityName(city.getName());
            res.setCityPopulation(city.getPopulation());
            res.setCityDistrict(city.getDistrict());

            Country country = city.getCountry();
            res.setAlternativeCountryCode(country.getAlternativeCode());
            res.setContinent(country.getContinent());
            res.setCountryCode(country.getCode());
            res.setCountryName(country.getName());
            res.setCountryPopulation(country.getPopulation());
            res.setCountryRegion(country.getRegion());
            res.setCountrySurfaceArea(country.getSurfaceArea());

            Set<CountryLanguage> countryLanguages = country.getLanguages();
            Set<Language> languages = countryLanguages.stream().map(c1 -> {
                Language language = new Language();
                language.setLanguage(c1.getLanguage());
                language.setOfficial(c1.getOfficial());
                language.setPercentage(c1.getPercentage());
                return language;
            }).collect(Collectors.toSet());
            res.setLanguages(languages);

            return res;
                }).collect(Collectors.toList());
    }

    private RedisClient prepareRedisClient(){
        RedisClient redisClient = RedisClient.create(RedisURI.create("localhost", 6379));
        try(StatefulRedisConnection<String,String> connection = redisClient.connect()){
            System.out.println("\nConnected to Redis I'M HEEEEEEEEEEEEEEEEEREEEEEEEEEEE\n");
        }
        return redisClient;
    }

    private void pushToRedis(List<CityCountry> data){
        try(StatefulRedisConnection<String,String> connection = redisClient.connect()){
            RedisStringCommands<String,String> sync = connection.sync();
            for (CityCountry cityCountry : data) {
                try {
                    sync.set(String.valueOf(cityCountry.getId()), mapper.writeValueAsString(cityCountry));
                }
                catch (JsonProcessingException e){
                    e.printStackTrace();
                }
            }
        }
    }

    private void testRedisData(List<Integer> ids){
        try(StatefulRedisConnection<String,String> connection =  redisClient.connect()){
            RedisStringCommands<String,String> sync = connection.sync();
            for (Integer id : ids) {
                String value = sync.get(String.valueOf(id));
                try{
                    mapper.readValue(value,CityCountry.class);
                }
                catch (JsonProcessingException e){
                    e.printStackTrace();
                }
            }
        }
    }

    private void testMySqlData(List<Integer> ids){
        try(Session session = sessionFactory.getCurrentSession()){
            session.beginTransaction();
            for (Integer id : ids) {
                City city = cityDAO.getById(id);
                Set<CountryLanguage> languages = city.getCountry().getLanguages();
            }
            session.getTransaction().commit();
        }
    }

    public static void main(String[] args) {
        Main main = new Main();
        List<City> allCities = main.fetchData(main);
        List<CityCountry> preparedData = main.transformData(allCities);
        main.pushToRedis(preparedData);

        main.sessionFactory.getCurrentSession().close();

        List<Integer> ids = List.of(1,995,1493,2499,2992,10,777, 3469,3998,26);

        long startRedis = System.currentTimeMillis();
        main.testRedisData(ids);
        long endRedis = System.currentTimeMillis();

        long startMySql = System.currentTimeMillis();
        main.testMySqlData(ids);
        long endMySql = System.currentTimeMillis();

        System.out.println("Redis: " + (endRedis - startRedis) + " ms");
        System.out.println("MySql: " + (endMySql - startMySql) + " ms");

        main.shutdown();
    }
}
