/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id: CanvasController.java,v 1.7 2002-08-09 06:53:08 nataliya Exp $
 */
package org.tockit.canvas.controller;

import org.tockit.canvas.Canvas;
import org.tockit.canvas.CanvasItem;
import org.tockit.canvas.events.*;
import org.tockit.events.EventBroker;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Point2D;
import java.util.Timer;

public class CanvasController implements MouseListener, MouseMotionListener {

    private Canvas canvas;

    private EventBroker eventBroker;

    /**
     * Flag to prevent label from being moved when just clicked on
     */
    private boolean dragMode = false;

    /**
     * This is true if a popup might have been opened as reaction on a mouse press
     * event.
     */
    private boolean popupOpen = false;

    /**
     * Distance that label has to be moved to enable dragMode
     */
    private int dragMin = 5;

    /**
     * The position where the mouse was when the last event came.
     */
    private Point2D lastMousePos = null;

    /**
     * A timer to distinguish between single and double clicks.
     */
    private Timer doubleClickTimer = null;

    /**
     * Holds the selected CanvasItem
     * that the user has clicked on with intent to move
     */
    private CanvasItem selectedCanvasItem = null;

    public CanvasController(Canvas canvas, EventBroker eventBroker) {
        this.canvas = canvas;
        this.eventBroker = eventBroker;
        canvas.addMouseListener(this);
        canvas.addMouseMotionListener(this);
    }

    /**
     * Not used -- mouse clicks are handled as press/release combinations.
     */
    public void mouseClicked(MouseEvent e) {
    }

    /**
     * Handles mouse release events.
     *
     * Resets the diagram from dragging mode back into normal mode or calls
     * singleClicked() or doubleClicked() on the CanvasItem hit. If no item was hit
     * backgroundSingleClicked() or backgroundDoubleClicked() on the canvas is
     * called.
     *
     * singleClicked() will only be send if it is not a double click. In any case
     * clicked() or backgroundClicked() will be send.
     *
     * @todo Use system double click timing instead of hard-coded 300ms
     */
    public void mouseReleased(MouseEvent e) {
        if (popupOpen) {
            popupOpen = false;
            return; // nothing to do, we react only on normal clicks
        }
        Point screenPos = e.getPoint();
        if (e.isPopupTrigger()) {
            Point2D canvasPos = canvas.getCanvasCoordinates(screenPos);
            handlePopupRequest(e.getModifiers(), canvasPos, screenPos);
            popupOpen = true;
        }
        if (dragMode) {
            dragMode = false;
            dragFinished(e);
            canvas.repaint();
        } else {
            Point2D modelPos = null;
            modelPos = canvas.getCanvasCoordinates(screenPos);
            this.eventBroker.processEvent(
                    new CanvasItemClickedEvent(this.selectedCanvasItem,
                                               e.getModifiers(), modelPos, screenPos));
            if (e.getClickCount() == 1) {
                this.doubleClickTimer = new Timer();
                this.doubleClickTimer.schedule(
                        new CanvasItemSingleClickTask(this.selectedCanvasItem,
                                e.getModifiers(), modelPos, screenPos, eventBroker), 300);
            } else if (e.getClickCount() == 2) {
                this.doubleClickTimer.cancel();
                this.eventBroker.processEvent(
                        new CanvasItemActivatedEvent(selectedCanvasItem,
                                                     e.getModifiers(), modelPos, screenPos));
            }
        }
        selectedCanvasItem = null;
    }

    protected void dragFinished(MouseEvent e) {
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }

    /**
     * Handles dragging the canvas items.
     */
    public void mouseDragged(MouseEvent e) {
        if (selectedCanvasItem == null) {
            return;
        }
        Point mousePos = e.getPoint();
        if (!canvas.contains(mousePos)) {
            return;
        }
        if (!dragMode && (lastMousePos.distance(mousePos) >= dragMin)) {
            dragMode = true;
        }
        if (dragMode) {
            Point2D mousePosTr = null;
            Point2D lastMousePosTr = null;
            mousePosTr = canvas.getCanvasCoordinates(mousePos);
            lastMousePosTr = canvas.getCanvasCoordinates(lastMousePos);
            this.eventBroker.processEvent(new CanvasItemDraggedEvent(
                    this.selectedCanvasItem,
                    e.getModifiers(),
                    lastMousePosTr, lastMousePos,
                    mousePosTr, mousePos));
            lastMousePos = mousePos;
        }
    }

    public void mouseMoved(MouseEvent e) {
        /*
        if (dragMode) {
            return;
        }
        Point2D mousePos = e.getPoint();
        Point2D canvasPos = canvas.getCanvasCoordinates(mousePos);
        CanvasItem pointedItem = canvas.getCanvasItemAt(canvasPos);
        System.out.println("pointedItem = " + pointedItem);
        if (pointedItem == null) {
            return;
        }
        this.eventBroker.processEvent(new CanvasItemPointedEvent(
                                            pointedItem,
                                            e.getModifiers(),
                                            canvasPos,
                                            mousePos));
        */
    }

    /**
     * Finds, raises and stores the canvas item hit.
     */
    public void mousePressed(MouseEvent e) {
        Point screenPos = e.getPoint();
        Point2D canvasPos = canvas.getCanvasCoordinates(screenPos);
        this.selectedCanvasItem = canvas.getCanvasItemAt(canvasPos);
        this.lastMousePos = screenPos;
        if (e.isPopupTrigger()) {
            handlePopupRequest(e.getModifiers(), canvasPos, screenPos);
        }
        this.popupOpen = e.isPopupTrigger();
    }

    private void handlePopupRequest(int modifiers, Point2D canvasPos, Point screenPos) {
        this.eventBroker.processEvent(new CanvasItemContextMenuRequestEvent(
                this.selectedCanvasItem, modifiers, canvasPos, screenPos));
    }
}
