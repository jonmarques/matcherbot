package br.com.sauran.matcher.entities;

public class Player {

	private String slug;
	private String location;
	private String firstname;
	private String lastname;
	private String role;

	public Player(String slug, String location, String firstname, String lastname, String role) {
		this.slug = slug;
		this.location = location;
		this.firstname = firstname;
		this.lastname = lastname;
		this.role = role;
	}

	public String getSlug() {
		return slug;
	}

	public String getLocation() {
		return location;
	}

	public String getFirstname() {
		return firstname;
	}

	public String getLastname() {
		return lastname;
	}

	public String getRole() {
		return role;
	}
}
