package org.fenixedu.spaces.domain;

import org.joda.time.DateTime;
import org.joda.time.Interval;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.Atomic.TxMode;

public class Space extends Space_Base {

    public Space(Information information) {
        setCreated(new DateTime());
        add(information);
    }

    /**
     * get the most recent space information
     * 
     * @return
     * @throws UnavailableException
     */
    Information getInformation() throws UnavailableException {
        return getInformation(new DateTime());
    }

    /**
     * get the most recent space information valid at the specified datetime.
     * 
     * @param when
     * @return
     * @throws UnavailableException
     */

    Information getInformation(DateTime when) throws UnavailableException {
        return getInformation(when, new DateTime());
    }

    /**
     * get the space information valid at the specified when date, created on atWhatDate.
     * 
     * @param when
     * @param atWhatDate
     * @return
     */

    Information getInformation(final DateTime when, final DateTime creationDate) throws UnavailableException {
        Information current = getCurrent();
        while (current != null) {
            if (current.contains(when)) {
                return current;
            }
            current = current.getPrevious();
        }
        throw new UnavailableException();
    }

    public Integer getCapacity() throws UnavailableException {
        return getCapacity(new DateTime());
    }

    public Integer getCapacity(DateTime when) throws UnavailableException {
        return getInformation(when).getAllocatableCapacity();
    }

    @Atomic(mode = TxMode.WRITE)
    protected void add(Information information) {
        if (getCurrent() == null) {
            setCurrent(information);
            return;
        }
        final DateTime newStart = information.getValidFrom();
        final DateTime newEnd = information.getValidUntil();

        final Interval newValidity = information.getValidity();

        Information newCurrent = null;
        Information last = null;
        Information newHead = null;

        Information current = getCurrent();
        Information head = current;
        Interval currentValidity = current.getValidity();

        boolean foundEnd = false;
        boolean foundStart = false;

        // insert at head
        if (newValidity.isAfter(currentValidity)) {
            newHead = information;
            newHead.setPrevious(head);
        }
        if (newHead == null) {
            while (current != null) {
                if (!foundEnd && !foundStart && current.contains(newValidity)) {
                    Information right = current.keepRight(newEnd);
                    if (last != null) {
                        last.setPrevious(right);
                    }
                    right.setPrevious(information);
                    last = information;
                    newCurrent = current.keepLeft(newStart);
                    foundEnd = true;
                    foundStart = true;
                } else {
                    if (!foundEnd && current.contains(newEnd)) {
                        Information right = current.keepRight(newEnd);
                        if (last != null) {
                            last.setPrevious(right);
                        }
                        last = right;
                        newCurrent = information;
                        foundEnd = true;
                    }
                    if (foundEnd && current.contains(newStart)) {
                        newCurrent = current.keepLeft(newStart);
                        foundStart = true;
                    } else {
                        if (!foundEnd || foundStart) {
                            newCurrent = current.copy();
                        }
                    }
                }

                //bookkeeping code
                if (last != null) {
                    last.setPrevious(newCurrent);
                }

                last = newCurrent;

                if (newHead == null) {
                    newHead = newCurrent;
                }

                current = current.getPrevious();
            }

            //insert at end
            if (!foundEnd) {
                last.setPrevious(information);
            }
        }

        addHistory(head);
        setCurrent(newHead);
    }
}
