package game;

import javax.swing.ImageIcon;

public class MemoryCard {
    private ImageIcon image;
    private boolean matched;

    public MemoryCard(ImageIcon image) {
        if (image == null) {
            throw new IllegalArgumentException("Image cannot be null");
        }
        this.image = image;
        this.matched = false;
    }

    public ImageIcon getImage() {
        return image;
    }

    public boolean isMatched() {
        return matched;
    }

    public void setMatched(boolean matched) {
        this.matched = matched;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        MemoryCard card = (MemoryCard) obj;
        return image.getDescription().equals(card.image.getDescription());
    }

    @Override
    public int hashCode() {
        return image.getDescription().hashCode();
    }
}
