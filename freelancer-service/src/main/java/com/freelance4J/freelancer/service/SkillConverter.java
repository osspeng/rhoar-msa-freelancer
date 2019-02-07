package com.freelance4J.freelancer.service;

import java.util.TreeSet;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class SkillConverter implements AttributeConverter<TreeSet<String>, String> {

 private static final String SEPARATOR = ",";

 @Override
 public String convertToDatabaseColumn(TreeSet<String> skills) {
  StringBuilder sb = new StringBuilder();

  for (String skill : skills) {
	  sb.append(skill).append(SEPARATOR);
  }
  return sb.toString();
 }

 @Override
 public TreeSet<String> convertToEntityAttribute(String skillString) {
  String[] skillArray = skillString.split(SEPARATOR);
  TreeSet<String> skills = new TreeSet<String>();
  for (String skill : skillArray) {
	  skills.add(skill);
  }
  return skills;
 }

}
