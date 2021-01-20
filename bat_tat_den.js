radio.onReceivedString(function (receivedString) {
    cmdOut = receivedString
    if (cmdOut == "1") {
        basic.showLeds(`
            # . . . .
            . # . . .
            . . # . .
            . . . # .
            . . . . #
            `)
    } else {
        if (cmdOut == "2") {
            basic.showLeds(`
                . . . . #
                . . . # .
                . . # . .
                . # . . .
                # . . . .
                `)
        } else {
            if (cmdOut == "3") {
                basic.showLeds(`
                    . . # . .
                    . . # . .
                    . . # . .
                    . . # . .
                    . . # . .
                    `)
            } else {
                if (cmdOut == "4") {
                    basic.showLeds(`
                        # # # # #
                        # # # # #
                        # # # # #
                        # # # # #
                        # # # # #
                        `)
                }
            }
        }
    }
})
serial.onDataReceived(serial.delimiters(Delimiters.Hash), function () {
    cmd = serial.readUntil(serial.delimiters(Delimiters.Hash))
    if (cmd == "1") {
        basic.showLeds(`
            # . . . .
            . # . . .
            . . # . .
            . . . # .
            . . . . #
            `)
    } else {
        if (cmd == "2") {
            basic.showLeds(`
                . . . . #
                . . . # .
                . . # . .
                . # . . .
                # . . . .
                `)
        } else {
            if (cmd == "3") {
                basic.showLeds(`
                    . . # . .
                    . . # . .
                    . . # . .
                    . . # . .
                    . . # . .
                    `)
            } else {
                if (cmd == "4") {
                    basic.showLeds(`
                        # # # # #
                        # # # # #
                        # # # # #
                        # # # # #
                        # # # # #
                        `)
                }
            }
        }
    }
    radio.sendString(cmd)
})
let cmd = ""
let cmdOut = ""
radio.setGroup(100)
serial.redirectToUSB()
