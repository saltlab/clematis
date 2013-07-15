package com.clematis.core.episode;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.clematis.core.trace.TraceObject;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.introspect.VisibilityChecker;

@Path("/clematis-api")
@Produces({ "application/json" })
public class episodeResource {

	private Story s1;
	private ObjectMapper mapper = new ObjectMapper();
	private Map<String, Episode> episodeMap = new HashMap<String, Episode>(200);

	public void configureObjectMapper() {
		mapper.setVisibilityChecker(VisibilityChecker.Std.defaultInstance()
		        .withFieldVisibility(
		                Visibility.ANY));
		mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
		mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
		// to allow coercion of JSON empty String ("") to null Object value:
		mapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
		mapper.enable(SerializationFeature.INDENT_OUTPUT);
		mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

	}

	public String intialize() {

		configureObjectMapper();
		try {
			this.s1 = mapper.readValue(new File("story.json"),
			        Story.class);
		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		for (int i = 0; i < s1.getEpisodes().size(); i++) {
			episodeMap.put(Integer.toString(i), s1.getEpisodes().get(i));
		}

		return "successfully intialized story";

	}

	@GET
	@Path("/episodes/howmany")
	@Produces(MediaType.APPLICATION_JSON)
	public int NumberOfEpisodes() {
		intialize();
		return this.s1.getEpisodes().size();
	}

	@GET
	@Path("/episodes")
	@Produces(MediaType.APPLICATION_JSON)
	public List<Episode> getEpisodes() {
		intialize();
		return this.s1.getEpisodes();

	}

	@GET
	@Path("/episodes/pretty")
	@Produces(MediaType.APPLICATION_JSON)
	public String getEpisodesPretty() {
		intialize();
		try {
			return mapper.writeValueAsString(this.s1.getEpisodes());
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	@GET
	@Path("/episodes/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Episode getEpisode(@PathParam("id") String id) {
		intialize();
		return episodeMap.get(id);
	}

	@GET
	@Path("/episodes/{id}/DOM")
	@Produces(MediaType.APPLICATION_JSON)
	public String getEpisodeDom(@PathParam("id") String id)
	{
		intialize();

		if (episodeMap.get(id).getDom() == null) {
			return "DOM is NULL!";
		}
		else {
			return episodeMap.get(id).getDom();
		}
	}

	@GET
	@Path("/episodes/{id}/source")
	@Produces(MediaType.APPLICATION_JSON)
	public TraceObject getEpisodeSource(@PathParam("id") String id) {
		intialize();
		return episodeMap.get(id).getSource();
	}

	@GET
	@Path("/episodes/{id}/trace")
	@Produces(MediaType.APPLICATION_JSON)
	public EpisodeTrace getEpisodeTrace(@PathParam("id") String id) {
		intialize();
		return episodeMap.get(id).getTrace();
	}

}
