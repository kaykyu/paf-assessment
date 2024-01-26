package vttp2023.batch4.paf.assessment.repositories;

import java.util.List;
import java.util.Optional;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import vttp2023.batch4.paf.assessment.Utils;
import vttp2023.batch4.paf.assessment.models.Accommodation;
import vttp2023.batch4.paf.assessment.models.AccommodationSummary;

@Repository
public class ListingsRepository {

	// You may add additional dependency injections

	@Autowired
	private MongoTemplate template;

	/*
	 * Write the native MongoDB query that you will be using for this method
	 * inside this comment block
	 * 
	 * db.listings.distinct("address.suburb",
	 * {"address.suburb": {$nin: ["", null]}})
	 */
	public List<String> getSuburbs(String country) {
		Query query = new Query(Criteria.where("address.suburb").nin("", null)
				.and("address.country").regex(country, "i"));
		return template.findDistinct(query, "address.suburb", "listings", String.class);
	}

	/*
	 * Write the native MongoDB query that you will be using for this method
	 * inside this comment block
	 * 
	 * db.listings.find({
	 * "address.suburb": {$regex: "suburb", $options: "i"},
	 * price: {$lte: priceRange},
	 * accommodates: {$gte: persons},
	 * min_nights: {$lte: duration}},
	 * {name: 1, accommodates: 1, price: 1})
	 * .sort({price: 1});
	 */
	public List<AccommodationSummary> findListings(String suburb, int persons, int duration, float priceRange) {

		Query query = new Query(Criteria.where("address.suburb").regex(suburb, "i")
				.and("price").lte(priceRange)
				.and("accommodates").gte(persons)
				.and("min_nights").lte(duration));
		query.fields().include("name", "accommodates", "price");
		query.with(Sort.by(Direction.DESC, "price"));
		
		return template.find(query, Document.class, "listings")
				.stream()
				.map(d -> {
					AccommodationSummary acc = new AccommodationSummary();
					acc.setId(d.getString("_id"));
					acc.setName(d.getString("name"));
					acc.setAccomodates(d.getInteger("accommodates"));
					acc.setPrice(d.get("price", Number.class).floatValue());
					return acc;
				})
				.toList();
	}

	// IMPORTANT: DO NOT MODIFY THIS METHOD UNLESS REQUESTED TO DO SO
	// If this method is changed, any assessment task relying on this method will
	// not be marked
	public Optional<Accommodation> findAccommodatationById(String id) {
		Criteria criteria = Criteria.where("_id").is(id);
		Query query = Query.query(criteria);

		List<Document> result = template.find(query, Document.class, "listings");
		if (result.size() <= 0)
			return Optional.empty();

		return Optional.of(Utils.toAccommodation(result.getFirst()));
	}

}
