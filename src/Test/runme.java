/*  This file is part of Euchre App.
 *
 *  Copyright 2012 Bryan Bueter
 *
 *  Euchre App is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Euchre App is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Euchre App.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package Test;

import Game.*;

// Create a test game creation
public class runme {
    private Euchre game;

    runme() {
        this.game = new Euchre();

        this.game.getDeck().shuffle();
        this.game.getDeck().shuffle();
        this.game.getDeck().shuffle();
        this.game.getDeck().print();
    }

    public static void main(String args[]) {
        new runme();
    }
}
