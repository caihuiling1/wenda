package com.nowcoder;

import com.nowcoder.dao.QuestionDAO;
import com.nowcoder.dao.UserDAO;
import com.nowcoder.model.EntityType;
import com.nowcoder.model.Question;
import com.nowcoder.model.User;
import com.nowcoder.service.FollowService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Date;
import java.util.Random;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = WendaApplication.class)
@Sql("/init-schema.sql")
public class InitDatabaseTests {
	@Autowired
	UserDAO userDAO;

	@Autowired
	QuestionDAO questionDAO;

	@Autowired
	FollowService followService;

	@Test
	public void contextLoads() {
		Random random = new Random();
		for(int i = 0; i < 11; ++i){
			User user = new User();
			user.setHeadUrl(String.format("https://static.nowcoder.net/head/%dm.png", random.nextInt(1000)));
			user.setName(String.format("USER%d", i));
			user.setSalt("");
			user.setPassword("");
			userDAO.addUser(user);

			//互相关注
			for(int j = 1; j < i; j++){
				followService.follow(j, EntityType.ENTITY_USER, i);
			}

			user.setPassword("xx");
			userDAO.updatePassword(user);

			Question question = new Question();
			question.setCommentCount(i);
			Date date = new Date();
			date.setTime(date.getTime() + 1000*3600*i);
			question.setCreatedDate(date);
			question.setUserId(i+1);
			question.setTitle(String.format("TILTE{%d}", i));
			question.setContent(String.format("Balalalalalal content %d", i));

			questionDAO.addQuestion(question);
		}

		Assert.assertEquals("xx", userDAO.selectById(1).getPassword());
		userDAO.deleteById(1);
		Assert.assertNull(userDAO.selectById(1));

		//System.out.println(questionDAO.selectLatestQuestions(0, 0, 10));
	}

}
