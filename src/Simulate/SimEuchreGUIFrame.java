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

package Simulate;

import java.awt.*;

// Create a window with a simulated euchre game
public class SimEuchreGUIFrame extends javax.swing.JFrame {

    public SimEuchreGUIFrame(SimEuchreCanvas board) {
        setBackground(new Color(130, 50, 40));
        setLayout(new BorderLayout(3, 3));
        add(board, BorderLayout.CENTER);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        this.setSize(358, 378);
        //this.setSize(696, 696);
        this.setTitle("Euchre");
        this.setVisible(true);
    }

    public static void main(final String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                if ( args.length == 1 ) {
                    if ( args[0].equals("-winPct") ) {
                            new SimEuchreGUIFrame(new SimEuchreCanvasWinPct());
                    } else {
                        System.err.println("Options: -winPct (display win%)");
                        System.exit(1);
                    }
                } else {
                    new SimEuchreGUIFrame(new SimEuchreCanvas());
                }
            }
        });
    }
}
