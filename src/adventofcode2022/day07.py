from pprint import pprint
import re

exampleInput: str = """$ cd /
$ ls
dir a
14848514 b.txt
8504156 c.dat
dir d
$ cd a
$ ls
dir e
29116 f
2557 g
62596 h.lst
$ cd e
$ ls
584 i
$ cd ..
$ cd ..
$ cd d
$ ls
4060174 j
8033020 d.log
5626152 d.ext
7214296 k"""


def parseCommand(input: str):
    result = re.search(
        "\$ ([a-z]*)\s?(.*)?",
        input,
    )
    groups = result.groups()
    return {"cmd": groups[0], "arguments": groups[1], "outputs": []}


def parseOutput(input: str):
    result = re.search(
        "([a-zA-Z0-9]*) ([a-zA-Z0-9.]*)",
        input,
    )
    groups = result.groups()
    type = "dir" if groups[0] == "dir" else "file"
    output = {
        "type": type,
    }
    if type == "dir":
        output["name"] = groups[1]
    if type == "file":
        output["name"] = groups[1]
        output["size"] = int(groups[0])
    return output


def parseCommands(input: str):
    lines = input.splitlines()
    commands = []
    current_cmd = None

    for i, line in enumerate(lines):
        if line.startswith("$"):
            # if the current line is a command, that means the previous command is finished and we can append that one to the list of commands
            if current_cmd is not None:
                commands.append(current_cmd)
                current_cmd = None
            current_cmd = parseCommand(line)
            # Current line is a command output
        else:
            current_cmd["outputs"] = current_cmd["outputs"] or []
            current_cmd["outputs"].append(parseOutput(line))

        if i + 1 == len(lines):
            commands.append(current_cmd)

    return commands


def main():
    commands = parseCommands(exampleInput)
    # construct tree from commands
    # Walk the tree and recursively find file sizes for each directory, return the directories
    # filter directories with file size too large
    # sum up the remaining file sizes
    pprint(commands)


if __name__ == "__main__":
    main()
