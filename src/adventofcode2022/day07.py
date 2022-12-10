from pprint import pprint
import re

example_input: str = """$ cd /
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


def get_input():
    with open("./resources/inputs/day07.txt") as f:
        contents = f.read()
        return contents


def parse_command(input: str):
    result = re.search(
        "\$ ([a-z]*)\s?(.*)?",
        input,
    )
    groups = result.groups()
    return {"cmd": groups[0], "arguments": groups[1], "outputs": []}


def parse_output(input: str):
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


def parse_commands(input: str):
    lines = input.splitlines()
    commands = []
    current_cmd = None

    for i, line in enumerate(lines):
        if line.startswith("$"):
            # if the current line is a command, that means the previous command is finished and we can append that one to the list of commands
            if current_cmd is not None:
                commands.append(current_cmd)
                current_cmd = None
            current_cmd = parse_command(line)
            # Current line is a command output
        else:
            current_cmd["outputs"] = current_cmd["outputs"] or []
            current_cmd["outputs"].append(parse_output(line))

        if i + 1 == len(lines):
            commands.append(current_cmd)

    return commands


def collect_files_from_commands(commands):
    files = []
    path = []
    for cmd in commands:
        if cmd["cmd"] == "cd":
            if cmd["arguments"] == "..":
                path = path[:-1]
                continue
            path = path + [cmd["arguments"]]
        if cmd["cmd"] == "ls":
            for output in cmd["outputs"]:
                if output["type"] == "file":
                    files.append(
                        {"path": path + [output["name"]], "size": output["size"]}
                    )
    return files


def directory_sizes(files):
    dir_sizes = {}
    for file in files:
        dirs = file["path"][:-1]
        while dirs:
            dir_path = "/".join(dirs)
            dir_sizes[dir_path] = dir_sizes.get(dir_path) or 0
            dir_sizes[dir_path] += file["size"]
            dirs = dirs[:-1]
    return dir_sizes


def dirs_lte(dirs, lte):
    return {k: v for k, v in dirs.items() if v <= lte}


def dirs_gte(dirs, gte):
    return {k: v for k, v in dirs.items() if v >= gte}


def sum_dirs(dirs):
    sum = 0
    for _, v in dirs.items():
        sum += v
    return sum


def sum_dirs_less_than(input, less_than):
    commands = parse_commands(input)
    files = collect_files_from_commands(commands)
    dirsizes = directory_sizes(files)
    return sum_dirs(dirs_lte(dirsizes, less_than))


def dir_to_delete_for_update(input):
    commands = parse_commands(input)
    files = collect_files_from_commands(commands)
    dirsizes = directory_sizes(files)
    total_disk_space = 70000000
    free_space_needed = 30000000
    used_space = dirsizes.get("/")
    disk_space_to_free = free_space_needed - (total_disk_space - used_space)
    large_enough_dirs = dirs_gte(dirsizes, disk_space_to_free).values()
    return sorted(large_enough_dirs)[0]


def main():
    # pprint(sum_dirs_less_than(example_input, 100_000))
    # pprint(sum_dirs_less_than(get_input(), 100_000))
    # pprint(dir_to_delete_for_update(example_input))
    pprint(dir_to_delete_for_update(get_input()))


if __name__ == "__main__":
    main()
