#pragma once

class TileMap : public sf::Drawable, public sf::Transformable {

public:

	bool load(const std::string& tileset, sf::Vector2u tileSize, int* tiles , unsigned int width, unsigned int height, unsigned int multiplier);

private:

	virtual void draw(sf::RenderTarget& target, sf::RenderStates states) const;

};