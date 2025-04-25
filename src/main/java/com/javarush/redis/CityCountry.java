package com.javarush.redis;

import com.javarush.domain.Continent;

import java.math.BigDecimal;
import java.util.Set;

public class CityCountry{
    private Integer id;

    private String cityName;

    private String cityDistrict;

    private Integer cityPopulation;

    private String countryCode;

    private String alternativeCountryCode;

    private String countryName;

    private Continent continent;

    private String countryRegion;

    private BigDecimal countrySurfaceArea;

    private Integer countryPopulation;

    private Set<Language> languages;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String name) {
        this.cityName = name;
    }

    public String getCityDistrict() {
        return cityDistrict;
    }

    public void setCityDistrict(String district) {
        this.cityDistrict = district;
    }

    public Integer getCityPopulation() {
        return cityPopulation;
    }

    public void setCityPopulation(Integer population) {
        this.cityPopulation = population;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getAlternativeCountryCode() {
        return alternativeCountryCode;
    }

    public void setAlternativeCountryCode(String alternativeCountryCode) {
        this.alternativeCountryCode = alternativeCountryCode;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public Continent getContinent() {
        return continent;
    }

    public void setContinent(Continent continent) {
        this.continent = continent;
    }

    public String getCountryRegion() {
        return countryRegion;
    }

    public void setCountryRegion(String countryRegion) {
        this.countryRegion = countryRegion;
    }

    public BigDecimal getCountrySurfaceArea() {
        return countrySurfaceArea;
    }

    public void setCountrySurfaceArea(BigDecimal countrySurfaceArea) {
        this.countrySurfaceArea = countrySurfaceArea;
    }

    public Integer getCountryPopulation() {
        return countryPopulation;
    }

    public void setCountryPopulation(Integer countryPopulation) {
        this.countryPopulation = countryPopulation;
    }

    public Set<Language> getLanguages() {
        return languages;
    }

    public void setLanguages(Set<Language> languages) {
        this.languages = languages;
    }
}
