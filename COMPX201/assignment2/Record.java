/**
  Implements the class for storing information about a record
  */
class Record{
	private String genre;
	private int year;
	private String artist;
	private String title;

	public Record(String g, int y, String a, String t){
		genre = g;
		year = y;
		artist = a;
		title = t;
	}

	/**
	  @return the value of the genre field
	  */
	public String getGenre(){
		return genre;
	}

	/**
	  @return the value of the year field
	  */
	public int getYear(){
		return year;
	}

	/**
	  @return the value of the artist field
	  */
	public String getArtist(){
		return artist;
	}

	/**
	  @return the value of the title field
	  */
	public String getTitle(){
		return title;
	}

	/**
	  @return a string representing the information stored about the record
	  */
	public String toString(){
		return getGenre() + " | " + getYear() + " | " + getArtist() + " | " + getTitle();
	}

	/**
	  @param Record other, the Record to compare to
	  @return a positive number if this record should be ordered after the passed object, a negative number if it should be ordered before, and 0 otherwise
	  */
	public int compareTo(Record other){
		int genreComp = this.getGenre().compareTo(other.getGenre());
		if(genreComp < 0){
			return -4;
		}else if(genreComp > 0){
			return 4;
		}else{
			if(this.getYear() < other.getYear()){
				return -3;
			}else if(this.getYear() > other.getYear()){
				return 3;
			}else{
				int artistComp = this.getArtist().compareTo(other.getArtist());
				if(artistComp < 0){
					return -2;
				}else if(artistComp > 0){
					return 2;
				}else{
					int titleComp = this.getTitle().compareTo(other.getTitle());
					if(titleComp < 0){
						return -1;
					}else if(titleComp > 0){
						return 1;
					}else{
						return 0;
					}
				}
			}
		}
	}
}
