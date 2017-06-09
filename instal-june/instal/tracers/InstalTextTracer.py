
import sys

from .InstalTracer import InstalTracer
from instal.instaljsonhelpers import json_dict_to_string


class InstalTextTracer(InstalTracer):
    """
        InstalTextTracer
        Implementation of ABC InstalTracer for text output.
        Will produce same output as instalsolve's verbose=1 option.
    """

    def trace_to_file(self):
        start = 0 if self.zeroth_term else 1
        f = None
        if self.output_file_name == "-":
            f = sys.stdout
        with open(self.output_file_name, 'w') if not f else f as tfile:
            for i in range(start, len(self.trace)):
                t = self.trace[i]
                print(json_dict_to_string(t) + "\n", file=tfile)
