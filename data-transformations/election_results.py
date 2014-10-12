from builtins import property

__author__ = 'Maciej Bilas'

import pandas as pd
from pandas import DataFrame, Series
import seaborn as sn
from collections import namedtuple
from enum import Enum

import os.path

__script_path = os.path.dirname(__file__)

_base_dir = os.path.join(__script_path, '..')

sample_results_filepath = os.path.realpath(os.path.join(
    _base_dir, "Raw data/csv files/Izbori 2010/PARLAMENT BiH/FBiH/2010_FBiH_511_IZBORNA_JEDINICA_1.csv"
))

ElectionKeyBase = namedtuple('ElectionKey', ['file', 'election_type', 'year', 'election_unit'])


class ElectionKey(ElectionKeyBase):
    @property
    def full_path(self):
        return os.path.realpath(os.path.join(
            _base_dir, 'Raw data', 'csv files', 'Izbori %d' % self.year, self.election_type.value, self.file
        ))


class ElectionType(Enum):
    parliament_bih = 'PARLAMENT BiH'


election_results = [
    ElectionKey('FBiH/2010_FBiH_511_IZBORNA_JEDINICA_1.csv', ElectionType.parliament_bih,
                2010, 511)
]


def load_key(key: ElectionKey) -> DataFrame:
    df = load_file(key.full_path)
    df = drop_totals(df)
    df = rename_special_ballot_labels(df, key.election_unit)
    df = unstack(df)

    return df


def load_file(file: str) -> DataFrame:
    df = pd.read_csv(file, sep='\t', index_col=0, encoding='utf-16', header=0)
    # Drop empty rows http://thomas-cokelaer.info/blog/2014/05/pandas-read_csv-how-to-skip-empty-lines/
    df.dropna(how='all', inplace=True)
    return df


def drop_totals(df: DataFrame) -> DataFrame:
    df.drop('Ukupno:', inplace=True)  # Drop summary row
    df.drop('Ukupno za biračko mjesto:', axis=1, inplace=True)
    return df


def rename_special_ballot_labels(df: DataFrame, election_unit: int) -> DataFrame:
    index_series = df.index.to_series()
    eu = lambda s: s % election_unit
    # http://pandas.pydata.org/pandas-docs/stable/generated/pandas.Series.replace.html
    index_series.replace(to_replace=['POŠTOM', 'ODUSTVO', 'POTVRÐENI'],
                         value=[eu("%d-post"), eu("%d-absent"), eu("%d-confirmed")],
                         inplace=True)

    df.set_index(index_series, inplace=True)

    return df


def compute_municipality_id_column(df : DataFrame) -> DataFrame:
    # TODO
    pass


def unstack(df: DataFrame) -> DataFrame:
    return df.unstack().reset_index()


def sample() -> DataFrame:
    return load_key(election_results[0])
