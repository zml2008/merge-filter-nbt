/*
 * merge-filter-nbt -- A filter for comparing NBT data in Araxis Merge
 * Copyright (C) 2021 zml
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package ca.stellardrift.nbtmerge;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Collectors;
import net.kyori.adventure.nbt.BinaryTagIO;
import net.kyori.adventure.nbt.CompoundBinaryTag;
import net.kyori.adventure.nbt.TagStringIO;

/**
 * A filter for Araxis Merge to convert NBT to and from binary.
 */
public class NbtMergeFilter {

  enum Mode {
    BIN_TO_TEXT,
    TEXT_TO_BIN
  }

  public static void main(final String[] args) {
    Mode mode = null;
    Path from = null;
    Path to = null;
    Path meta = null;
    int pointer = 0;

    for(final var arg : args) {
      switch(arg) {
        case "-f":
          mode = Mode.BIN_TO_TEXT;
          break;
        case "-r":
          mode = Mode.TEXT_TO_BIN;
          break;
        default:
          switch(pointer++) {
            case 0:
              from = Path.of(arg);
              break;
            case 1:
              to = Path.of(arg);
              break;
            case 2:
              meta = Path.of(arg);
              break;
            default:
              System.err.println("Too many arguments!");
              System.exit(1);
              return;
          }
          break;
      }
    }

    if(mode == null || from == null || to == null) {
      System.err.println("Invalid arguments, usage [-f|-r] <from> <to> <meta>");
      System.exit(1);
      return;
    }

    try {
      final var stringIo = TagStringIO.builder().indent(4).build();
      if(mode == Mode.BIN_TO_TEXT) {
        final CompoundBinaryTag tag = BinaryTagIO.unlimitedReader().read(from, BinaryTagIO.Compression.GZIP);
        try(final var writer = Files.newBufferedWriter(to, StandardCharsets.UTF_8)) {
          stringIo.toWriter(tag, writer);
        }
      } else if(mode == Mode.TEXT_TO_BIN) {
        try(final var reader = Files.newBufferedReader(from, StandardCharsets.UTF_8)) {
          final CompoundBinaryTag tag = stringIo.asCompound(reader.lines().collect(Collectors.joining("\n")));
          BinaryTagIO.writer().write(tag, to, BinaryTagIO.Compression.GZIP);
        }
      }
    } catch(final IOException ex) {
      System.err.println("Failed to convert NBT");
      ex.printStackTrace();
      System.exit(-1);
    }
  }
}
