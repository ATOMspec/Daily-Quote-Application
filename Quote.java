public class Quote {
    private String text;
    private String author;
    private boolean favorite;

    public Quote(String text, String author) {
        this.text = text;
        this.author = author;
        this.favorite = false;
    }

    public String getText() {
        return text;
    }

    public String getAuthor() {
        return author;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

    @Override
    public String toString() {
        return "\"" + text + "\" - " + author + (favorite ? " [Favorite]" : "");
    }
}