package com.guitar.db;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.guitar.db.model.Location;
import com.guitar.db.repository.LocationJpaRepository;


@ContextConfiguration(locations={"classpath:com/guitar/db/applicationTests-context.xml"})
@RunWith(SpringJUnit4ClassRunner.class)
public class LocationPersistenceTests {
	/*@Autowired
	private LocationRepository locationRepository;
*/
	@PersistenceContext
	private EntityManager entityManager;
	
	@Autowired
	private LocationJpaRepository locationJpaRepository; 

	
	@Test
	public void testJpaFind(){
		List<Location> locations =  locationJpaRepository.findAll();
		assertNotNull(locations);
	}
	
	
	@Test
	public void testJpaAnd(){
		List<Location> locations =  locationJpaRepository.findByStateAndCountry("Alaska", "United States");
		assertNotNull(locations);
		assertEquals("Alaska", locations.get(0).getState());
	}
	
	@Test
	public void testJpaOr(){
		List<Location> locations =  locationJpaRepository.findByStateOrCountry("Alaska", "Alaska");
		assertNotNull(locations);
		assertEquals("Alaska", locations.get(0).getState());
	}
	
	@Test
	public void testJpaIsOr(){
		List<Location> locations =  locationJpaRepository.findByStateIsOrCountryEquals("Utah", "United States");
		System.out.println(locations.get(0).getCountry());
		System.out.println(locations.get(0).getState());
		
		assertNotNull(locations);
		assertEquals("United States", locations.get(0).getCountry());
	}
	
	
	@Test
	public void testJpaNot(){
		List<Location> locations =  locationJpaRepository.findByStateNot("Alaska");
		assertNotNull(locations);
		assertNotSame("Alaska", locations.get(0).getState());
	}
	
	@Test
	@Transactional
	public void testSaveAndGetAndDelete() throws Exception {
		Location location = new Location();
		location.setCountry("Canada");
		location.setState("British Columbia");
		location = locationJpaRepository.saveAndFlush(location);
		
		// clear the persistence context so we don't return the previously cached location object
		// this is a test only thing and normally doesn't need to be done in prod code
		entityManager.clear();

		Location otherLocation = locationJpaRepository.findOne(location.getId());
		assertEquals("Canada", otherLocation.getCountry());
		assertEquals("British Columbia", otherLocation.getState());
		
		//delete BC location now
		locationJpaRepository.delete(otherLocation);
	}

	@Test
	public void testFindWithLike() throws Exception {
		List<Location> locs = locationJpaRepository.findByStateLike("New%");
		assertEquals(4, locs.size());
	}
	
	@Test
	public void testFindWithNotLike() throws Exception {
		List<Location> locs = locationJpaRepository.findByStateNotLike("New%");
		assertEquals(46, locs.size());
	}
	
	@Test
	public void testFindWithNotLikeAsc() throws Exception {
		List<Location> locs = locationJpaRepository.findByStateNotLikeOrderByStateAsc("New%");
		assertEquals(46, locs.size());
		
		locs.forEach((location) -> {
			System.out.println(location.getState());
		});
	}
	
	@Test
	public void testFindWithNotLikeDesc() throws Exception {
		List<Location> locs = locationJpaRepository.findByStateNotLikeOrderByStateDesc("New%");
		assertEquals(46, locs.size());
		
		locs.forEach((location) -> {
			System.out.println(location.getState());
		});
	}
	
	@Test
	public void testFindStartingWith() throws Exception {
		List<Location> locs = locationJpaRepository.findByStateStartingWith("New");
		assertEquals(4, locs.size());
	}

	@Test
	public void testFindIgnoreCaseStartingWith() throws Exception {
		List<Location> locs = locationJpaRepository.findByStateIgnoreCaseStartingWith("new");
		assertEquals(4, locs.size());
	}
	
	@Test
	public void testFindFirstIgnoreCaseStartingWith() throws Exception {
		Location locs = locationJpaRepository.findFirstByStateIgnoreCaseStartingWith("a");
		assertEquals("Alabama", locs.getState());
	}
	
	
	@Test
	public void testFindTop4IgnoreCaseStartingWith() throws Exception {
		List<Location> locs = locationJpaRepository.findTop5ByStateIgnoreCaseStartingWith("a");
		assertEquals(4, locs.size());
	}
	
	
	@Test
	public void testFindEndingWith() throws Exception {
		List<Location> locs = locationJpaRepository.findByStateEndingWith("a");
		assertEquals(21, locs.size());
	}
	

	@Test
	public void testFindContaining() throws Exception {
		List<Location> locs = locationJpaRepository.findByStateContaining("New");
		assertEquals(4, locs.size());
	}
	
	@Test
	@Transactional  //note this is needed because we will get a lazy load exception unless we are in a tx
	public void testFindWithChildren() throws Exception {
		Location arizona = locationJpaRepository.findOne(3L);
		assertEquals("United States", arizona.getCountry());
		assertEquals("Arizona", arizona.getState());
		
		assertEquals(1, arizona.getManufacturers().size());
		
		assertEquals("Fender Musical Instruments Corporation", arizona.getManufacturers().get(0).getName());
	}
}
